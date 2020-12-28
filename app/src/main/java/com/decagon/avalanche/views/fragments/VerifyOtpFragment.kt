package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chaos.view.PinView
import com.decagon.avalanche.R
import com.decagon.avalanche.data.User
import com.decagon.avalanche.databinding.FragmentVerifyOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class VerifyOtpFragment : Fragment() {
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    lateinit var pinFromUser: PinView
    lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var addNewUser: User

    private val args: VerifyOtpFragmentArgs by navArgs()

    //SafeArgs Data
    private lateinit var phoneNumber: String
    private lateinit var pwd: String
    private lateinit var lName: String
    private lateinit var fName: String
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        phoneNumber = args.userNumber
        pwd = args.userPwd
        lName = args.userLName
        fName = args.userFName
        email = args.userEmail

        addNewUser = User(fName, lName, email, phoneNumber, pwd, false)

        Log.d("TAG", "onVerificationCompleted:$phoneNumber")
        Log.d("TAG", "onVerificationCompleted:$email")

        //hooks
        pinFromUser = binding.pinView



        binding.verifyBtn.setOnClickListener {
            val code = pinFromUser.text.toString()
            if (code.isNotEmpty()) {
                verifyVerificationCode(code)
            } else {
                Toast.makeText(requireContext(), "Code not received", Toast.LENGTH_LONG).show()
            }

        }


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted:$credential")
                val code = credential.smsCode
                if (code != null && code != "code") {
                    pinFromUser.setText(code)
                    verifyVerificationCode(code)
                }


                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(requireContext(), "Invalid request", Toast.LENGTH_LONG).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(requireContext(), "Too many requests", Toast.LENGTH_LONG).show()
                }

                // Show a message and update the UI
                findNavController().navigate(R.id.signUpFragment)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        sendVerificationCode(phoneNumber)

        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(requireContext(), "Verification completed.", Toast.LENGTH_LONG)
                        .show()

                    val user = task.result?.user
                    Log.d("TAG", "signInWithCredential:success. $user")

                    storeNewUserDataInFirebase()
                    findNavController().navigate(R.id.mainFragment)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                            requireContext(),
                            "Verification code is invalid. Try again!",
                            Toast.LENGTH_LONG
                        ).show()

                        pinFromUser.setText("")
                    }
                }
            }
    }

    private fun storeNewUserDataInFirebase() {
        val rootNode = FirebaseDatabase.getInstance()
        val reference = rootNode.getReference("Users")
        reference.child(email).setValue(addNewUser)
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}