package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentForgotPasswordBinding
import com.decagon.avalanche.databinding.FragmentForgotPwdSucesssMessageBinding
import com.decagon.avalanche.databinding.FragmentSetNewPasswordBinding
import com.decagon.avalanche.databinding.FragmentVerifyOtpBinding


class ForgotPwdSuccessMessageFragment : Fragment() {
    private var _binding: FragmentForgotPwdSucesssMessageBinding? = null
    private val binding get() = _binding!!

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