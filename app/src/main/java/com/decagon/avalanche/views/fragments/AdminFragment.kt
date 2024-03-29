package com.decagon.avalanche.views.fragments

import android.app.Activity
import android.app.ProgressDialog
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
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.decagon.avalanche.R
import com.decagon.avalanche.cloudinary.CloudinaryManager
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.data.PushNotification
import com.decagon.avalanche.data.PushNotificationData
import com.decagon.avalanche.databinding.FragmentAdminBinding
import com.decagon.avalanche.firebase.FirebaseReference
import com.decagon.avalanche.network.RetroInstance
import com.decagon.avalanche.utils.showToast
import com.decagon.avalanche.views.TOPIC
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    lateinit var progressBar: ProgressBar
    lateinit var image: ImageView
    lateinit var title: Editable
    lateinit var price: Editable
    lateinit var desc: Editable

    private val PICK_IMAGE_CODE = 0
    lateinit var url: String

    private var mProgressDialog: ProgressDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        url = ""

        //MediaManager
        CloudinaryManager()

        //Obtain data from inputs
        image = binding.productImageIv
        title = binding.productNameEt.text!!
        price = binding.productPriceEt.text!!
        desc = binding.productDescEt.text!!

//        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        binding.adminFragmentSubmitBtn.setOnClickListener {
            if (url.isBlank() || url == "") {
                showToast("Please select an Image to proceed", requireActivity())
            }
        }

        binding.selectProductImageBtn.setOnClickListener {
            selectImageIntent()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }
    }

    private fun selectImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE)
    }

    private fun saveProductToFirebase(url: String) {
        when {
            title.isEmpty() -> {
                binding.productNameEt.error = "Please input a title"
                showToast("Please fill title field", requireActivity())
            }
            price.isEmpty() -> {
                binding.productPriceEt.error = "Price field cannot be empty"
                showToast("Please fill price field", requireActivity())
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
                    mProgressDialog!!.dismiss()
                    showToast("Product saved to database successfully", requireActivity())

                    PushNotification(
                        PushNotificationData(
                            "New product", "$title | N$price"),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }

                    /** Clear input fields */

                    title.clear()
                    price.clear()
                    desc.clear()
                    image.setImageURI(null)

                }
                    .addOnFailureListener {
                        // Write failed
                        showToast("Product not added successfully. Please check input fields and try again",
                            requireActivity())
                        mProgressDialog!!.dismiss()
                    }
            }
        }

    }

    private fun uploadToCloudinary(filepath: Uri?) {
        MediaManager.get().upload(filepath).unsigned("avalanche").callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                showToast("Image upload successfully", requireActivity())

                /** get upload url from callback resultData */
                MediaManager.get().url().generate(resultData?.entries?.forEach {
                    if (it.key == "secure_url") {
                        //Log.d("TAG", "URL: ${it.key}, ${it.value}")
                        url = it.value as String
                    }
                }.toString())


                //Save product into firebase
                binding.adminFragmentSubmitBtn.setOnClickListener {
                    mProgressDialog =
                        ProgressDialog.show(
                            requireActivity(),
                            "Saving Product",
                            "Please wait...",
                            false,
                            false
                        )

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


    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetroInstance.api.postNotification(notification)

                if (response.isSuccessful) {
                    Log.d("TAG", "sendNotificationSuccess: ${
                        Gson().toJson(response.body())
                    }")
                } else {
                    Log.e("TAG", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("TAG", "sendNotification: $e")
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}