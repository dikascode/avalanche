package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.os.UserManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var phone: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var progressBar: ProgressBar
    lateinit var countryCodePicker: CountryCodePicker

    lateinit var userManager: com.decagon.avalanche.preferencesdatastore.UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        userManager = com.decagon.avalanche.preferencesdatastore.UserManager(requireContext())

        //hooks
        phone = binding.userPhoneEt
        password = binding.userPasswordEt
        progressBar = binding.progressBarLayout.fragmentMainProgressBar
        countryCodePicker = binding.countryCodePicker

        binding.loginBtn.setOnClickListener {
            letUserLoggedIn()
        }

        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.forgotPwdBtn.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }
        return binding.root
    }

    private fun letUserLoggedIn() {
        if (!validateInputFields()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        var _phone = phone.editText?.text.toString().trim()
        val _password = password.editText?.text.toString().trim()

        if (_phone[0] == '0') {
            _phone = _phone.substring(1)
        }

        val _enteredNumber = "+" + countryCodePicker.fullNumber + _phone

        //DB query
        val checkUser =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNumber")
                .equalTo(_enteredNumber)

        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    phone.error = null
                    phone.isErrorEnabled = false

                    val systemPassword =
                        snapshot.child(_enteredNumber).child("password")
                            .getValue(String::class.java)

                    if (systemPassword != null) {
                        if (systemPassword == _password) {
                            password.error = null
                            password.isErrorEnabled = false

                            val fname = snapshot.child(_enteredNumber).child("firstName").getValue(String::class.java)
//                            val lname = snapshot.child(_enteredNumber).child("lastName").getValue(String::class.java)
                            val email =
                                snapshot.child(_enteredNumber).child("email")
                                    .getValue(String::class.java)
                            val phone = snapshot.child(_enteredNumber).child("phoneNumber").getValue(String::class.java)

                            //Save user data to DataStore
                            GlobalScope.launch {
                                userManager.storeUser(fname!!, email!!, phone!!)
                            }

                            userManager.userPhoneFlow.asLiveData().observe(requireActivity(), {
                                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG)
                                    .show()
                            })

                            findNavController().navigate(R.id.mainFragment)
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Password does not match!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No such user exists!", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun validateInputFields(): Boolean {
        val _phone = phone.editText?.text.toString().trim()
        val _password = password.editText?.text.toString().trim()

        return when {
            _phone.isEmpty() -> {
                phone.error = "Email cannot be empty"
                phone.requestFocus()

                password.error = null
                password.isErrorEnabled = false
                false
            }
            _password.isEmpty() -> {
                password.error = "Password cannot be empty"
                password.requestFocus()

                phone.error = null
                phone.isErrorEnabled = false
                false
            }
            else -> {
                password.error = null
                password.isErrorEnabled = false

                phone.error = null
                phone.isErrorEnabled = false
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}