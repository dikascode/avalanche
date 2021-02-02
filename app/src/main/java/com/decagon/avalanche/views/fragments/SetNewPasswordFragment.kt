package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentSetNewPasswordBinding
import com.decagon.avalanche.utils.showToast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SetNewPasswordFragment : Fragment() {
    private var _binding: FragmentSetNewPasswordBinding? = null
    private val binding get() = _binding!!

    lateinit var fireBaseReference: DatabaseReference

    lateinit var password: TextInputLayout
    lateinit var confirmPassword: TextInputLayout

    lateinit var _newPassword: String
    lateinit var _confirmPassword: String
    lateinit var progressBar: ProgressBar

    private val args: SetNewPasswordFragmentArgs by navArgs()

    //SafeArgs Data
    private lateinit var phoneNumber: String

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Handle back press
         */
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }

            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetNewPasswordBinding.inflate(inflater, container, false)

        fireBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        phoneNumber = args.phoneNumber

        password = binding.newPasswordEt
        confirmPassword = binding.confirmPasswordEt
        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        binding.nextBtn.setOnClickListener {
            setNewPassword()

            progressBar.visibility = View.GONE
        }


        return binding.root
    }

    private fun setNewPassword() {
        if (!validatePassword() || !validateConfirmPassword()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        fireBaseReference.child(phoneNumber).child("password").setValue(_newPassword)

        showToast("Password Updated Successfully", requireActivity())

        findNavController().navigate(R.id.forgotPwdSuccessMessageFragment)
    }

    private fun validatePassword(): Boolean {
        _newPassword = password.editText?.text.toString().trim()

        return when {
            _newPassword.isEmpty() -> {
                password.error = "Password field cannot be empty"
//                password.requestFocus()
                false
            }
            _newPassword.length < 6 -> {
                password.error = "Password cannot be less than 6 characters"
//                password.requestFocus()
                false
            }
            else -> {
                password.error = null
                password.isErrorEnabled = false
                true
            }
        }

    }

    private fun validateConfirmPassword(): Boolean {
        _newPassword = password.editText?.text.toString().trim()
        _confirmPassword = confirmPassword.editText?.text.toString().trim()
        return when {
            _confirmPassword.isEmpty() -> {
                confirmPassword.error = "Confirm password field cannot be empty"
//                confirmPassword.requestFocus()
                false
            }

            _confirmPassword != _newPassword -> {
                confirmPassword.error = "Confirm password and new password do not match"
                confirmPassword.requestFocus()
                false
            }

            else -> {
                confirmPassword.error = null
                confirmPassword.isErrorEnabled = false
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}