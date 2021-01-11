package com.decagon.avalanche.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.decagon.avalanche.CloudinaryManager
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
    lateinit var url:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        url = ""

        CloudinaryManager()

        //Obtain data from inputs
        image = binding.productImageIv
        title = binding.productNameEt.text!!
        price = binding.productPriceEt.text!!
        desc = binding.productDescEt.text!!

        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        binding.adminFragmentSubmitBtn.setOnClickListener {
            if(url.isBlank() || url == "") {
                makeToast("Please select an Image to proceed")
            }
        }

        binding.selectProductImageBtn.setOnClickListener {
            selectImageIntent()
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

    private fun saveProductToFirebase(url: String) {
        if (title != null && price != null && desc != null) {
            when {
                title.isEmpty() -> {
                    binding.productNameEt.error = "Please input a title"
                    makeToast("Please fill title field")
                }
                price.isEmpty() -> {
                    binding.productPriceEt.error = "Price field cannot be empty"
                    makeToast("Please fill price field")
                }

                desc.isEmpty() -> {
                    binding.productDescEt.error = "Description field cannot be empty"
                }
                else -> {
                    //save data to firebase
                    val newProduct = Product(title.toString(),
                        url,
                        price.toString().toDouble(),
                        desc.toString(),
                        true)
                    val reference = FirebaseReference.productReference

                    reference.child(title.toString()).setValue(newProduct).addOnSuccessListener {
                        // Write was successful!
                        progressBar.visibility = View.GONE
                        makeToast("Product saved to database successfully")

                        /**
                         * Clear input fields
                         */

                        title.clear()
                        price.clear()
                        desc.clear()

                    }
                        .addOnFailureListener {
                            // Write failed
                            makeToast("Product not added successfully. Please check input fields and try again")
                            progressBar.visibility = View.GONE
                        }
                }
            }
        }

    }

    private fun uploadToCloudinary(filepath: Uri?) {
        MediaManager.get().upload(filepath).unsigned("avalanche").callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                makeToast("Image upload successfully")

                /**
                 * get upload url from callback resultData
                 */
                MediaManager.get().url().generate(resultData?.entries?.forEach {
                    if (it.key == "secure_url") {
                        Log.d("TAG", "URL: ${it.key}, ${it.value}")
                        url = it.value as String
                    }
                }.toString())


                //Save product into firebase
                binding.adminFragmentSubmitBtn.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    saveProductToFirebase(url)
                }

            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.d("TAG", "onError: $error")
            }

            override fun onStart(requestId: String?) {
                Log.d("TAG", "onStart: $requestId")
            }
        }).dispatch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                image.setImageURI(data?.data)

                Log.d("TAG", "image: ${data?.data.toString()}")

                //Save image to cloudinary
                uploadToCloudinary(data?.data)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeToast(str: String) {
        Toast.makeText(requireActivity(),
            str,
            Toast.LENGTH_LONG).show()
    }

}