package com.decagon.avalanche.views.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import at.favre.lib.crypto.bcrypt.BCrypt
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentLoginBinding
import com.decagon.avalanche.firebase.FirebaseReference
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    lateinit var rememberMe: CheckBox

    lateinit var userManager: com.decagon.avalanche.preferencesdatastore.UserManager

    val reference = FirebaseReference.userReference

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        userManager = com.decagon.avalanche.preferencesdatastore.UserManager(requireActivity())

        //hooks
        phone = binding.userPhoneEt
        password = binding.userPasswordEt
        progressBar = binding.progressBarLayout.fragmentMainProgressBar
        countryCodePicker = binding.countryCodePicker
        rememberMe = binding.rememberMe


        /**
         * Set phone and password into login edit fields if rememberMe already saved
         */
        checkAndImplementRememberMe()


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

    private fun checkAndImplementRememberMe() {
        userManager.rmUserPhoneFlow.asLiveData().observe(requireActivity(), { phoneNumber ->
            if (phoneNumber != "") {
                phone.editText?.setText(phoneNumber)
                rememberMe.isChecked = true
            }
        })

        userManager.rmUserPasswordFlow.asLiveData().observe(requireActivity(), { pwd ->
            if (pwd != "") {
                password.editText?.setText(pwd)
            }
        })

        userManager.rmCountryCodeFlow.asLiveData().observe(requireActivity(), { code ->
            if (code != "") {
                countryCodePicker.setCountryForPhoneCode(code.toInt())
            }
        })
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

        if (rememberMe.isChecked) {
            //Save user login data to DataStore
            GlobalScope.launch {
                userManager.createRememberMeSession(_phone, _password, countryCodePicker.fullNumber)
            }
        }

        //DB query
        val checkUser =
            reference.orderByChild("phoneNumber")
                .equalTo(_enteredNumber)

        //Check if user exists
        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    phone.error = null
                    phone.isErrorEnabled = false

                    val hashPassword =
                        snapshot.child(_enteredNumber).child("password").value.toString()

                    //decrypt pwd
                    val result = BCrypt.verifyer().verify(_password.toCharArray(), hashPassword)

                    if (result.verified) {
                        password.error = null
                        password.isErrorEnabled = false

                        val fname = snapshot.child(_enteredNumber).child("firstName")
                            .getValue(String::class.java)
                        val lname = snapshot.child(_enteredNumber).child("lastName")
                            .getValue(String::class.java)
                        val email =
                            snapshot.child(_enteredNumber).child("email")
                                .getValue(String::class.java)
                        val phone = snapshot.child(_enteredNumber).child("phoneNumber")
                            .getValue(String::class.java)

                        val adminStatus = snapshot.child(_enteredNumber).child("admin")
                            .getValue(Boolean::class.java)

                        //Email sharedPref
                        requireActivity().getSharedPreferences("userEmail", MODE_PRIVATE).edit()
                            .putString("email", email).apply()

                        //Save user login data to DataStore
                        GlobalScope.launch {
                            userManager.storeUser(fname!!,
                                lname!!,
                                email!!,
                                phone!!,
                                adminStatus!!)
                        }

                        findNavController().navigate(R.id.mainFragment)
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireActivity(),
                            "Password does not match!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), "No such user exists!", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireActivity(), error.message, Toast.LENGTH_LONG).show()
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