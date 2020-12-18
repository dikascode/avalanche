package com.decagon.avalanche.views.fragments



import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.NetworkStatusChecker
import com.decagon.avalanche.R
import com.decagon.avalanche.adapters.CategoriesAdapter
import com.decagon.avalanche.adapters.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.room.AvalancheDatabase
import com.decagon.avalanche.room.RoomBuilder

import com.decagon.avalanche.viewmodels.ProductsListViewModel


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var db: AvalancheDatabase

    lateinit var viewModel: ProductsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)

        val networkConnection = NetworkStatusChecker(requireContext())
        viewModel = ViewModelProvider(this).get(ProductsListViewModel::class.java)

        networkConnection.observe(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                viewModel.showProducts()

                implementMainGridLayoutRecyclerView(viewModel.productsLiveData)

                //Build room database
                db = RoomBuilder.getDatabase(requireActivity().applicationContext)

                //Categories recycler view
                implementLinearRecyclerViewForCategories()

            }else {
                viewModel.productsLiveData.removeObservers(viewLifecycleOwner)
            }
        })

        return view

    }

    private fun implementMainGridLayoutRecyclerView(viewModelLiveData: MutableLiveData<List<Product>>) {
        val recyclerView = binding.fragmentMainRv

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

        //Observe live data from view model class
        viewModelLiveData.observe(viewLifecycleOwner, {
            recyclerView.apply {
                layoutManager = GridLayoutManager(activity, 2)
                adapter =
                    ProductsAdapter(
                        it as ArrayList<Product>,
                        requireActivity()
                    ) { extraTitle, extraImageUrl, photoView ->

                        //Shared elements transition animations
//                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            activity as AppCompatActivity,
//                            photoView,
//                            "photoToAnimate"
//                        )

                        val action =
                            MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                                extraTitle
                            )

                        findNavController().navigate(action)
                    }
            }

            //Hide Progressbar on load of products
            binding.progressBarLayout.fragmentMainProgressBar.visibility = View.GONE
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