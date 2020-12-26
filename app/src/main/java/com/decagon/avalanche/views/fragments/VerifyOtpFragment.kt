package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.chaos.view.PinView
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentVerifyOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.FirebaseCommonKtxRegistrar
import java.util.concurrent.TimeUnit


class VerifyOtpFragment : Fragment() {
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    lateinit var pinFromUser: PinView
    lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    val args: VerifyOtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        //SafeArgs Data
        val phoneNumber = args.userNumber
        Log.d("TAG", "onVerificationCompleted:$phoneNumber")

        val email = args.userEmail

        Log.d("TAG", "onVerificationCompleted:$email")
        val pwd = args.userPwd
        val lName = args.userLName
        val fName = args.userFName

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
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // ...
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