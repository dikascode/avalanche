package com.dikascode.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.dikascode.avalanche.databinding.FragmentForgotPasswordBinding
import com.dikascode.avalanche.databinding.FragmentVerifyOtpBinding
import com.dikascode.avalanche.utils.showToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker


class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    lateinit var phone: TextInputLayout
    lateinit var progressBar: ProgressBar
    //lateinit var countryCodePicker: CountryCodePicker
    lateinit var nextBtn: MaterialButton


    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        phone = binding.userPhoneEt
        progressBar = binding.progressBarLayout.fragmentMainProgressBar
//        countryCodePicker = binding.countryCodePicker
        nextBtn = binding.nextBtn

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        nextBtn.setOnClickListener {
            verifyPhoneNumber()
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun verifyPhoneNumber() {
        if (!validateInputFields()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        var _phone = phone.editText?.text.toString().trim()

        if (_phone[0] == '0') {
            _phone = _phone.substring(1)
        }

        val _enteredNumber = "+234$_phone"

        //DB query
        val checkUser =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNumber")
                .equalTo(_enteredNumber)

        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    phone.error = null
                    phone.isErrorEnabled = false

                    val action =
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToVerifyOtpFragment(
                            _enteredNumber,
                            "",
                            "",
                            "",
                            "",
                            "updateData"
                        )

                    findNavController().navigate(action)

                    progressBar.visibility = View.GONE

                } else {
                    progressBar.visibility = View.GONE
                   showToast("No such user exists!", requireActivity())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                showToast("An error occurred", requireActivity())
//                Toast.makeText(requireActivity(), error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun validateInputFields(): Boolean {
        val _phone = phone.editText?.text.toString().trim()

        return when {
            _phone.isEmpty() -> {
                phone.error = "Email cannot be empty"
                phone.requestFocus()
                false
            }
            else -> {
                phone.error = null
                phone.isErrorEnabled = false
                true
            }
        }
    }

}