package com.decagon.avalanche.ui.fragments


import android.content.Intent
import android.os.Bundle

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.adapters.CategoriesAdapter
import com.decagon.avalanche.adapters.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.room.AvalancheDatabase
import com.decagon.avalanche.room.RoomBuilder
import com.decagon.avalanche.ui.ProductDetails
import com.decagon.avalanche.viewmodels.ProductsListViewModel


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var db: AvalancheDatabase
    lateinit var viewModel: ProductsListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(ProductsListViewModel::class.java)
        viewModel.showProducts()

        implementMainGridLayoutRecyclerView(viewModel.productsLiveData)

        //Build room database
        db = RoomBuilder.getDatabase(requireActivity().applicationContext)

        //Categories recycler view
        implementLinearRecyclerViewForCategories()
        return view
    }

    private fun implementMainGridLayoutRecyclerView(viewModelLiveData: MutableLiveData<List<Product>>) {
        val recyclerView = binding.fragmentMainRv

        viewModelLiveData.observe(viewLifecycleOwner, {
            recyclerView.apply {
                layoutManager = GridLayoutManager(activity, 2)
                adapter =
                    ProductsAdapter(it as ArrayList<Product>) { extraTitle, extraImageUrl, photoView ->

                        //Go to product details when image is clicked
                        val intent = Intent(activity, ProductDetails::class.java)
                        intent.putExtra("title", extraTitle)
                        intent.putExtra("photo_url", extraImageUrl)

                        //Shared elements transition animations
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as AppCompatActivity,
                            photoView,
                            "photoToAnimate"
                        )
                        startActivity(intent, options.toBundle())
                    }
            }

            //Hide Progressbar on load of products
            binding.fragmentMainProgressBar.visibility = View.GONE
        })
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
                    val searchString = "$newText"

                    //Get search product from repository api method for search
                    viewModel.searchProducts(searchString)
                    implementMainGridLayoutRecyclerView(viewModel.productsSearchLiveData)

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