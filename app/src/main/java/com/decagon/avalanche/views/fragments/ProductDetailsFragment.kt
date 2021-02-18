package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.decagon.avalanche.constants.Constants.Companion.FORMATTER
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.databinding.FragmentProductDetailsBinding
import com.decagon.avalanche.firebase.FirebaseProducts
import com.decagon.avalanche.firebase.FirebaseReference
import com.decagon.avalanche.viewmodels.StoreViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProductDetailsFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var firebaseProducts: FirebaseProducts
    lateinit var mListener: ValueEventListener
    private val reference = FirebaseReference.productReference

    private lateinit var storeViewModel: StoreViewModel

    private val args: ProductDetailsFragmentArgs by navArgs()
    var productTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseProducts = FirebaseProducts()
//        firebaseProducts.getProductsFromFirebase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        productTitle = args.productTitle

//        Log.d("TAG", "filter: $productTitle")

        val checkProduct = reference.orderByChild("title").equalTo(productTitle)

       mListener = checkProduct.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.child(productTitle).getValue(Product::class.java)
                    if (product != null) {
                        bindProductDataToLayout(product)
                    }
//                    Log.d("TAG", "ProductDetail: ${product?.title}")
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: $error")
            }

        })


        //productDetails = firebaseProducts.getSingleProduct(productTitle)
        return view
    }

    private fun bindProductDataToLayout(productDetails: Product) {
        binding.productTitleTv.text = productDetails.title
        binding.productPriceTv.text = FORMATTER?.format(productDetails.price)
        binding.productDescTv.text = productDetails.desc
        val photoUrl = productDetails.photoUrl
        Picasso.get().load(photoUrl).into(binding.productImageIv)

        binding.addToCartBtn.setOnClickListener { _ ->
            val isAdded: Boolean = storeViewModel.addProductToCart(productDetails)

            if (isAdded) {
                Snackbar.make(
                    requireView(),
                    "${productDetails.title} added to cart.",
                    Snackbar.LENGTH_LONG
                )
            } else {
                Snackbar.make(
                    requireView(),
                    "Max quantity of 5 for item reached in cart",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        reference.removeEventListener(mListener)
    }
}