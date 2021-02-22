package com.dikascode.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.dikascode.avalanche.R
import com.dikascode.avalanche.databinding.FragmentForgotPasswordBinding
import com.dikascode.avalanche.databinding.FragmentForgotPwdSucesssMessageBinding
import com.dikascode.avalanche.databinding.FragmentSetNewPasswordBinding
import com.dikascode.avalanche.databinding.FragmentVerifyOtpBinding


class ForgotPwdSuccessMessageFragment : Fragment() {
    private var _binding: FragmentForgotPwdSucesssMessageBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Handle back press
         */
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPwdSucesssMessageBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}