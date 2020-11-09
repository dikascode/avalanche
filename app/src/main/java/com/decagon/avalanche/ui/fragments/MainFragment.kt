package com.decagon.avalanche.ui.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.decagon.avalanche.adapter.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.model.Product
import com.google.gson.Gson
import java.net.URL


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        //make network call on background thread
        val thread = Thread {
            try {
                //Your code goes here
                val json = URL("https://finepointmobile.com/data/products.json").readText()
                val products = Gson().fromJson(json, Array<Product>::class.java).toList()
//                Log.d("JSON", "json: $json")

                //Update ui on UI thread
                requireActivity().runOnUiThread(Runnable {
                    // Stuff that updates the UI
                    val recyclerView = binding.fragmentMainRv
                    recyclerView.apply {
                        layoutManager = GridLayoutManager(activity, 2)
                        adapter = ProductsAdapter(products as ArrayList<Product>)
                    }

                })

                //Make progress bar invisible after UI has been updated
                binding.fragmentMainProgressBar.visibility = View.GONE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        thread.start()

        //for(i in 0..10) products.add(Product("Lady Gown $i", "https://picsum.photos/id/1037/200", 4, 1000))


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}