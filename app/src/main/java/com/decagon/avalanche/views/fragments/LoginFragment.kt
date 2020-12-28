package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var email: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        //hooks
        email = binding.userEmailEt
        password = binding.userPasswordEt
        progressBar = binding.progressBarLayout.fragmentMainProgressBar



        binding.loginBtn.setOnClickListener {
            letUserLoggedIn()
        }

        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun letUserLoggedIn() {
        if (!validateInputFields()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        val _email = email.editText?.text.toString().trim()
        val _password = password.editText?.text.toString().trim()

        //DB query
        val checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email")
            .equalTo(_email)

        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    email.error = null
                    email.isErrorEnabled = false

                    val systemPassword =
                        snapshot.child(_email).child("password").getValue(String::class.java)

                    if (systemPassword != null) {
                        if (systemPassword == _password) {
                            password.error = null
                            password.isErrorEnabled = false

//                            val _fname = snapshot.child(_email).child("firstName").getValue(String::class.java)
//                            val _lname = snapshot.child(_email).child("lastName").getValue(String::class.java)
                            val  email = snapshot.child(_email).child("email").getValue(String::class.java)
//                            val _phone = snapshot.child(_email).child("phoneNumber").getValue(String::class.java)

                            Toast.makeText(requireContext(), "$email", Toast.LENGTH_LONG)
                                .show()

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
        val _email = email.editText?.text.toString().trim()
        val _password = password.editText?.text.toString().trim()

        return when {
            _email.isEmpty() -> {
                email.error = "Email cannot be empty"
                email.requestFocus()

                password.error = null
                password.isErrorEnabled = false
                false
            }
            _password.isEmpty() -> {
                password.error = "Password cannot be empty"
                password.requestFocus()

                email.error = null
                email.isErrorEnabled = false
                false
            }
            else -> {
                password.error = null
                password.isErrorEnabled = false

                email.error = null
                email.isErrorEnabled = false
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}