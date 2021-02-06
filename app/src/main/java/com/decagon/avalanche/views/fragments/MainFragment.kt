package com.decagon.avalanche.views.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.adapters.CategoriesAdapter
import com.decagon.avalanche.adapters.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.firebase.FirebaseReference
import com.google.firebase.database.*


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var product: Product
    lateinit var adapter: ProductsAdapter
    var productsList = ArrayList<Product>()

    lateinit var loggedOnSharePref: SharedPreferences

    private val reference = FirebaseReference.productReference

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Handle back press */
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }

            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        /** Save value to indicate user has reached main fragment previously and pass it through shared preference */
        loggedOnSharePref = requireContext().getSharedPreferences(
            "loggedOn",
            AppCompatActivity.MODE_PRIVATE
        )

        var editor: SharedPreferences.Editor = loggedOnSharePref.edit()
        editor.putBoolean("firstTime", false)
        editor.commit()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBarLayout.fragmentMainProgressBar.visibility = View.VISIBLE
        implementFirebase(reference)
    }

    private fun implementFirebase(reference: DatabaseReference) {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        product = dataSnapshot.getValue(Product::class.java)!!
                            productsList.add(product)
//                            Log.d("TAG", "size: ${productsList.size}")
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

        adapter =
            ProductsAdapter(
                productsList,
                requireActivity()
            ) { extraTitle, extraImageUrl, photoView ->
                val action =
                    MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                        extraTitle
                    )
//                Log.d("TAG", "param: $extraTitle")
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

    /**
     * Search
     */
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        requireActivity().menuInflater.inflate(R.menu.menu, menu)
//        val item: MenuItem = menu!!.findItem(R.id.action_search)
//        val searchView: SearchView = item.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText != null) {
//
//                    val filteredProducts = productsList.filter {
//                        it.title.contains(newText, true)
//                    }
//
//                    Log.d("TAG", "filter: ${filteredProducts.size}")
//
//                    //Get search product from firebase
//                    implementMainGridLayoutRecyclerView(filteredProducts as ArrayList<Product>)
//                    if (filteredProducts.isNotEmpty()) {
//                        binding.progressBarLayout.fragmentMainProgressBar.visibility = View.GONE
//                    } else {
////                        Toast.makeText(requireActivity(),
////                            "This product is not available yet. Search for another product",
////                            Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//                return false
//            }
//
//        })
//        super.onCreateOptionsMenu(menu, inflater)
//    }

}