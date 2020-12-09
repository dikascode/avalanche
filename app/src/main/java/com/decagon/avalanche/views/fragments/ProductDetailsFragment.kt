package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentProductDetailsBinding
import com.decagon.avalanche.viewmodels.ProductDetailsViewModel
import com.decagon.avalanche.viewmodels.StoreViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class ProductDetailsFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var storeViewModel: StoreViewModel

    private val args: ProductDetailsFragmentArgs by navArgs()
    var productTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productDetailsViewModel =
            ViewModelProvider(requireActivity()).get(ProductDetailsViewModel::class.java)
        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        productTitle = args.productTitle

        productDetailsViewModel.getProductByName(productTitle)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        productDetailsViewModel.productDetailsLiveData.observe(viewLifecycleOwner, {
            binding.productTitleTv.text = it.title
            binding.productPriceTv.text = it.price.toString()
            binding.productDescTv.text = it.desc
            val photoUrl = it.photoUrl
            Picasso.get().load(photoUrl).into(binding.productImageIv)

            binding.addToCartBtn.setOnClickListener { _ ->
                val isAdded: Boolean = storeViewModel.addProductToCart(it)

                if (isAdded) {
                    Snackbar.make(
                        requireView(),
                        "${it.title} added to cart.",
                        Snackbar.LENGTH_LONG
                    ).setAction("Checkout") {
                        requireView().findNavController().navigate(R.id.cartFragment)
                    }.show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Max quantity of 5 for item reached in cart",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}