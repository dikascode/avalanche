package com.decagon.avalanche.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.adapters.CategoriesAdapter
import com.decagon.avalanche.adapters.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.firebase.FirebaseReference
import com.decagon.avalanche.room.AvalancheDatabase
import com.google.firebase.database.*


class MainFragment : Fragment() {
    var productsList = ArrayList<Product>()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var product: Product

    lateinit var db: AvalancheDatabase
    lateinit var adapter: ProductsAdapter

    private val reference = FirebaseReference.productReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)

        implementFirebase(reference)
        //Build room database
        //db = RoomBuilder.getDatabase(requireActivity().applicationContext)

        return view

    }

    private fun implementFirebase(reference: DatabaseReference) {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        product = dataSnapshot.getValue(Product::class.java)!!
                        //Log.d("TAG", "Products: ${product?.title}")
                        if (!productsList.contains(product)) {
                            productsList.add(product)
                            Log.d("TAG", "size: ${productsList.size}")
                        }
                    }

                    implementMainGridLayoutRecyclerView(productsList)

                    //Hide Progressbar on load of products
                    if (productsList.size > 0) {
                        binding.progressBarLayout.fragmentMainProgressBar.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: $error")
            }

        })
    }


    private fun implementMainGridLayoutRecyclerView(productsList: ArrayList<Product>) {
        val recyclerView = binding.fragmentMainRv

        binding.progressBarLayout.fragmentMainProgressBar.visibility = View.VISIBLE

        binding.fragmentMainRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.fragmentMainRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
            )
        )

        adapter =
            ProductsAdapter(
                productsList,
                requireActivity()
            ) { extraTitle, extraImageUrl, photoView ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                        extraTitle
                    )

                Log.d("TAG", "param: $extraTitle")

                findNavController().navigate(action)
            }
        adapter.notifyDataSetChanged()

        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        recyclerView.adapter = adapter


    }

    private fun implementLinearRecyclerViewForCategories() {
        val categories =
            listOf("Mini-Skirt", "Palaso", "Native", "Wedding Gown", "Free Gown", "Special Styles")

        val categoriesRecyclerView = binding.categoriesRv
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = CategoriesAdapter(categories)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu, menu)
        val item: MenuItem = menu!!.findItem(R.id.action_search)
        val searchView: SearchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {

                    val filteredProducts = productsList.filter {
                        it.title.contains(newText, true)
                    }

                    //Log.d("TAG", "filter: ${filteredProducts.size}")

                    //Get search product from firebase
                    implementMainGridLayoutRecyclerView(filteredProducts as ArrayList<Product>)
                    if (filteredProducts.isNotEmpty()) {
                        binding.progressBarLayout.fragmentMainProgressBar.visibility = View.GONE
                    } else {
//                        Toast.makeText(requireContext(),
//                            "This product is not available yet. Search for another product",
//                            Toast.LENGTH_LONG).show()
                    }

                }

                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}