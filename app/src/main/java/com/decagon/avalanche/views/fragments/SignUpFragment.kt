package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentSignUpBinding
import com.decagon.avalanche.firebase.FirebaseReference
import com.decagon.avalanche.utils.showToast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    lateinit var firstName: TextInputLayout
    lateinit var lastName: TextInputLayout
    lateinit var email: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var phoneNumber: TextInputLayout
    lateinit var countryCodePicker: CountryCodePicker
    lateinit var progressBar: ProgressBar

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        //Hooks for getting data
        firstName = binding.userFirstnameEt
        lastName = binding.userLastnameEt
        email = binding.userEmailEt
        password = binding.userPasswordEt
        phoneNumber = binding.userNumberEt
        countryCodePicker = binding.countryCodePicker

        progressBar = binding.progressBarLayout.fragmentMainProgressBar

        binding.createAccountBtn.setOnClickListener {
            if (validateFirstName() && validateLastName() && validateEmail() && validatePhoneNumber() && validatePassword()) {
                passDataToVerifyOTPScreen()
            }
        }

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateFirstName(): Boolean {
        val string = firstName.editText?.text.toString().trim()

        return if (string.isEmpty()) {
            firstName.error = "Field cannot be empty"
            false
        } else {
            firstName.error = null
            firstName.isEnabled = false
            true
        }
    }

    private fun validateLastName(): Boolean {
        val string = lastName.editText?.text.toString().trim()

        return if (string.isEmpty()) {
            lastName.error = "Field cannot be empty"
            false
        } else {
            lastName.error = null
            lastName.isEnabled = false
            true
        }
    }

    private fun validateEmail(): Boolean {
        val string = email.editText?.text.toString().trim()
        val checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"

        return if (string.isEmpty()) {
            email.error = "Field cannot be empty"
            false
        } else if (!string.matches(checkEmail.toRegex())) {
            email.error = "Invalid Email!"
            false
        } else {
            email.error = null
            email.isEnabled = false
            true
        }
    }


    private fun validatePassword(): Boolean {
        val string = password.editText?.text.toString().trim()
        val checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                // "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                //"(?=S+$)" +           //no white spaces
                //".{4}" +               //at least 4 characters
                //"$";

                return if (string.isEmpty()) {
                    password.error = "Field cannot be empty"
                    false
                } else {
                    password.error = null
                    password.isEnabled = false
                    true
                }
    }


    private fun validatePhoneNumber(): Boolean {
        val string = phoneNumber.editText?.text.toString().trim()

        return if (string.isEmpty()) {
            phoneNumber.error = "Field cannot be empty"
            false
        } else {
            phoneNumber.error = null
            phoneNumber.isEnabled = false
            true
        }
    }

    private fun passDataToVerifyOTPScreen() {
        val _fName = firstName.editText?.text.toString().trim()
        val _lName = lastName.editText?.text.toString().trim()
        val _email = email.editText?.text.toString().trim()
        val _pwd = password.editText?.text.toString().trim()
        var _enteredPhoneNumber = phoneNumber.editText?.text.toString().trim()

        progressBar.visibility = View.VISIBLE


        if (_enteredPhoneNumber[0] == '0') {
            _enteredPhoneNumber = _enteredPhoneNumber.substring(1)
        }

        val _phoneNo = "+" + countryCodePicker.fullNumber + _enteredPhoneNumber


        //Verify if user exists
        val reference = FirebaseReference.userReference

        val checkUser =
            reference.orderByChild("phoneNumber")
                .equalTo(_phoneNo)

        checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    showToast("This user already exists!", requireActivity())

                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE

                    val action = SignUpFragmentDirections.actionSignUpFragmentToVerifyOtpFragment(
                        _phoneNo,
                        _fName,
                        _lName,
                        _pwd,
                        _email
                    )

                    findNavController().navigate(action)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                showToast("An error occurred", requireActivity())
//                Toast.makeText(requireActivity(), error.message, Toast.LENGTH_LONG).show()
            }

        })


    }

}