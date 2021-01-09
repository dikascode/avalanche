package com.decagon.avalanche.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.databinding.FragmentAdminBinding
import com.decagon.avalanche.firebase.FirebaseReference

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    lateinit var progressBar: ProgressBar

    private val PICK_IMAGE_CODE = 0

    //Obtain data from inputs
    private val image = binding.productImageIv
    private val title = binding.productNameEt.text
    private val price = binding.productPriceEt.text
    private val desc = binding.productDescEt.text

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        //Build room database
        // val db = RoomBuilder.getDatabase(requireActivity().applicationContext)

        binding.selectProductImageBtn.setOnClickListener {
            selectImageIntent()
        }

        binding.adminFragmentSubmitBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            //run room database logic in background thread
            val thread = Thread {
                try {
                    //Save product into room
                    saveProductToFirebase()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()
        }


        return view
    }

    private fun selectImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE)
    }

    private fun saveProductToFirebase() {
        Log.d("TAG", "saveProductToRoom: $title, $price")

        if (title != null && price != null && desc != null) {
            when {
                title.isEmpty() -> {
                    requireActivity().runOnUiThread(Runnable {
                        binding.productNameEt.error = "Please input a title"
                        Toast.makeText(requireContext(),
                            "Please fill title field",
                            Toast.LENGTH_LONG).show()
                    })

                }
                price.isEmpty() -> {
                    requireActivity().runOnUiThread(Runnable {
                        binding.productPriceEt.error = "Price field cannot be empty"
                        Toast.makeText(requireContext(),
                            "Please fill price field",
                            Toast.LENGTH_LONG).show()
                    })

                }

                desc.isEmpty() -> {
                    requireActivity().runOnUiThread {
                        binding.productDescEt.error = "Description field cannot be empty"
                    }
                }

                image == null -> {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            "Please choose an Image",
                            Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    //save data to firebase
                    val newProduct = Product(title.toString(),
                        "https://finepointmobile.com/api/ecommerce/redHat.jpg",
                        price.toString().toDouble(),
                        desc.toString(),
                        true)
                    val reference = FirebaseReference.productReference

                    reference.child(title.toString()).setValue(newProduct).addOnSuccessListener {
                        // Write was successful!
                        progressBar.visibility = View.GONE

                        Toast.makeText(requireContext(),
                            "Product saved to database successfully",
                            Toast.LENGTH_LONG).show()

                        /**
                         * Clear input fields
                         */

                        title.clear()
                        price.clear()
                        desc.clear()

                    }
                        .addOnFailureListener {
                            // Write failed
                            Toast.makeText(requireContext(),
                                "Product not added successfully. Please check input fields and try again",
                                Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.GONE
                        }

//                    val product = RoomProductModel(null, title.toString(), price.toString().toDouble(), "")
//                    ProductModel(db.productDao()).addProduct(product)
//                    db.close()

//                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.content_main_fl, MainFragment())
//                        .commit()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                image.setImageURI(data?.data)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}