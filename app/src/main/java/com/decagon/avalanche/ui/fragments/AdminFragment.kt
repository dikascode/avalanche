package com.decagon.avalanche.ui.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.decagon.avalanche.adapter.CategoriesAdapter
import com.decagon.avalanche.adapter.ProductsAdapter
import com.decagon.avalanche.databinding.FragmentAdminBinding
import com.decagon.avalanche.databinding.FragmentMainBinding
import com.decagon.avalanche.model.Product
import com.decagon.avalanche.room.AppDatabase
import com.decagon.avalanche.room.RoomBuilder
import com.decagon.avalanche.room.RoomProducts
import com.google.gson.Gson
import java.net.URL


class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.adminFragmentSubmitBtn.setOnClickListener {
            //make network call on background thread
            val thread = Thread {
                try {
                    //Your code goes here

                    //Build room database and save data into Room
                    val db = RoomBuilder.manageInstance(activity!!.applicationContext)

                    //Obtain data from input
                    val title = binding.productNameEt.text
                    val price: Double = binding.productPriceEt.text.toString().toDouble()

                    db.productDao().insertAll(RoomProducts(null, title.toString(), price))
                    db.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()
        }


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}