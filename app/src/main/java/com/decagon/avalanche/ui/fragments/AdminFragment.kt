package com.decagon.avalanche.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentAdminBinding
import com.decagon.avalanche.model.ProductModel
import com.decagon.avalanche.room.AvalancheDatabase
import com.decagon.avalanche.room.RoomBuilder
import com.decagon.avalanche.room.RoomProduct


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

        //Build room database
        val db = RoomBuilder.getDatabase(activity!!.applicationContext)

        binding.adminFragmentSubmitBtn.setOnClickListener {
            //run room database logic in background thread
            val thread = Thread {
                try {
                    //Save product into room
                    saveProductToRoom(db)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()
        }


        return view
    }

    private fun saveProductToRoom(db: AvalancheDatabase) {
        //Obtain data from inputs
        val title = binding.productNameEt.text
        val price = binding.productPriceEt.text

        Log.d("TAG", "saveProductToRoom: $title, $price")

        if (title != null && price!= null) {
            when {
                title.isEmpty() -> {
                    requireActivity().runOnUiThread(Runnable {
                        binding.productNameEt.error = "Please input a title"
                        Toast.makeText(requireContext(), "Please fill title field", Toast.LENGTH_LONG).show()
                    })

                }
                price.isEmpty() -> {
                    requireActivity().runOnUiThread(Runnable {
                        binding.productPriceEt.error = "Price field cannot be empty"
                        Toast.makeText(requireContext(), "Please fill price field", Toast.LENGTH_LONG).show()
                    })

                }
                else -> {
                    //save data to database
                    val product = RoomProduct(null, title.toString(), price.toString().toDouble())
                    ProductModel(db.productDao()).addProduct(product)
                    db.close()

                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.content_main_fl, MainFragment())
                        .commit()
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}