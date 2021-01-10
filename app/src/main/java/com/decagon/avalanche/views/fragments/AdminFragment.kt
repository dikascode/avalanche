package com.decagon.avalanche.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.databinding.FragmentAdminBinding
import com.decagon.avalanche.firebase.FirebaseReference


class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    lateinit var progressBar: ProgressBar
    lateinit var image: ImageView
    lateinit var title: Editable
    lateinit var price: Editable
    lateinit var desc: Editable

    private val PICK_IMAGE_CODE = 0
    lateinit var imageDataString: String

    var config: HashMap<String, String> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        //Obtain data from inputs
        image = binding.productImageIv
        title = binding.productNameEt.text!!
        price = binding.productPriceEt.text!!
        desc = binding.productDescEt.text!!


        //initialize MediaManager
        config["cloud_name"] = "di2lpinnp"
        config["api_key"] = "396379412919671"
        config["api_secret"] = "LPNhun_GmRbaVOGVYRosAkacJds"
        MediaManager.init(requireActivity(), config)

        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        binding.selectProductImageBtn.setOnClickListener {
            selectImageIntent()
            uploadToCloudinary(imageDataString)
        }

        binding.adminFragmentSubmitBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            //run room database logic in background thread
            val thread = Thread {
                try {
                    //Save image to cloudinary
//                    uploadToCloudinary(imageDataString)

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
                        Toast.makeText(requireActivity(),
                            "Please fill title field",
                            Toast.LENGTH_LONG).show()
                    })

                }
                price.isEmpty() -> {
                    requireActivity().runOnUiThread(Runnable {
                        binding.productPriceEt.error = "Price field cannot be empty"
                        Toast.makeText(requireActivity(),
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
                        Toast.makeText(requireActivity(),
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
                        Toast.makeText(requireActivity(),
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
                            Toast.makeText(requireActivity(),
                                "Product not added successfully. Please check input fields and try again",
                                Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.GONE
                        }
                }
            }
        }

    }

    private fun uploadToCloudinary(filepath: String) {
        MediaManager.get().upload(filepath).unsigned("avalanche").callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Toast.makeText(requireActivity(), "Task successful", Toast.LENGTH_SHORT).show()

                //Save product into firebase
                //saveProductToFirebase()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {

                Toast.makeText(requireActivity(), "Task Not successful" + error, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onStart(requestId: String?) {

                Toast.makeText(requireActivity(), "Start", Toast.LENGTH_SHORT).show()
            }
        }).dispatch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                image.setImageURI(data?.data)
                imageDataString = data?.data.toString()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}