package com.decagon.avalanche.ui.fragments


import android.os.Bundle

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.adapter.CategoriesAdapter
import com.decagon.avalanche.adapter.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.model.ProductModel
import com.decagon.avalanche.room.AvalancheDatabase
import com.decagon.avalanche.room.RoomBuilder
import com.decagon.avalanche.room.RoomProduct


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var db: AvalancheDatabase


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        //Build room database
        db = RoomBuilder.getDatabase(activity!!.applicationContext)


        //make network call on background thread
        val thread = Thread {
            try {
                //Your code goes here

                val productsFromDatabase = ProductModel(db.productDao()).readAllProducts()

                val products = mapProductListFromDatabaseQuery(productsFromDatabase)

                //Update ui on UI thread
                requireActivity().runOnUiThread(Runnable {
                    // Update recyclerview UI
                    recyclerViewGridLayout(products)
                })

                db.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()


        //Categories recycler view
        implementLinearRecyclerViewForCategories()

        return view
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

    private fun recyclerViewGridLayout(products: List<Product>) {
        val recyclerView = binding.fragmentMainRv
        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = ProductsAdapter(products as ArrayList<Product>)
        }

        //Make progress bar invisible after UI has been updated
        binding.fragmentMainProgressBar.visibility = View.GONE
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
                    val searchString = "%$newText%"

                    //make network call on background thread
                    val thread = Thread {
                        try {
                            //Your code goes here

                            val productsFromDatabase = ProductModel(db.productDao()).getSearchProducts(searchString)

                            val products = mapProductListFromDatabaseQuery(productsFromDatabase)

                            //Update ui on UI thread
                            requireActivity().runOnUiThread(Runnable {
                                // Update recyclerview UI
                                recyclerViewGridLayout(products)
                            })

                            db.close()


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                    thread.start()

                }

                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun mapProductListFromDatabaseQuery(productsFromDatabase: List<RoomProduct>): List<Product> {
        return productsFromDatabase.map {
            Product(
                it.title, "https://finepointmobile.com/data/jeans2.jpg", it.price, true
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}