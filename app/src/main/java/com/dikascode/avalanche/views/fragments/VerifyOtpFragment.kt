package com.dikascode.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.favre.lib.crypto.bcrypt.BCrypt
import com.chaos.view.PinView
import com.dikascode.avalanche.R
import com.dikascode.avalanche.api.JavaMailApi
import com.dikascode.avalanche.data.User
import com.dikascode.avalanche.databinding.FragmentVerifyOtpBinding
import com.dikascode.avalanche.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    lateinit var progressBar: ProgressBar

    lateinit var userManager: com.dikascode.avalanche.preferencesdatastore.UserManager

    private val args: VerifyOtpFragmentArgs by navArgs()

    //SafeArgs Data
    private lateinit var phoneNumber: String
    private lateinit var pwd: String
    private lateinit var lName: String
    private lateinit var fName: String
    private lateinit var email: String
    private lateinit var intention: String

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
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)

        //User Store Manager
        userManager = com.dikascode.avalanche.preferencesdatastore.UserManager(requireActivity())

        auth = FirebaseAuth.getInstance()

        phoneNumber = args.userNumber
        pwd = args.userPwd
        lName = args.userLName
        fName = args.userFName
        email = args.userEmail
        intention = args.intention

        progressBar = binding.progressBarLayout.fragmentMainProgressBar
        progressBar.visibility = View.VISIBLE

        //Encrypt pwd
        val passHash = BCrypt.withDefaults().hashToString(12, pwd.toCharArray())

        addNewUser = User(fName, lName, email, phoneNumber, passHash, false)

        //Log.d("TAG", "onVerificationCompleted:$intention")

        //hooks
        pinFromUser = binding.pinView

        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.welcomeFragment)
        }

        binding.verifyBtn.setOnClickListener {
            val code = pinFromUser.text.toString()
            if (code.isNotEmpty()) {
                verifyVerificationCode(code)
            } else {
                showToast("Code not received", requireActivity())
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
                    showToast("Invalid request", requireActivity())
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    showToast("Too many requests", requireActivity())
                }

                // Show a message and update the UI
                findNavController().navigate(R.id.signUpFragment)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
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
                    showToast("Verification completed.", requireActivity())

                    if (intention == "updateData") {
                        updateOldUserData()
                    } else {
                        storeNewUserDataInFirebase()
                        sendMail(email,
                            "Welcome to Avalanche",
                            "Thank you for signing up with Avalanche. Do enjoy a beautiful shopping experience.")
                        progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.mainFragment)
                    }


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
//                        showToast(
//                            "Verification code is invalid. Try again!", requireActivity())
                        pinFromUser.setText("")
                    }
                }
            }
    }

    private fun updateOldUserData() {
        val action =
            VerifyOtpFragmentDirections.actionVerifyOtpFragmentToSetNewPasswordFragment(phoneNumber)
        findNavController().navigate(action)
    }

    private fun storeNewUserDataInFirebase() {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(phoneNumber).setValue(addNewUser)

        //Save user sign up data to DataStore
        GlobalScope.launch {
            userManager.storeUser(fName, lName, email, phoneNumber, false)
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

    private fun sendMail(email: String, mailSubject: String, emailMessage: String) {
        val javaMailApi = JavaMailApi(requireActivity(),
            email,
            mailSubject,
            emailMessage)

        javaMailApi.execute()
    }

}