package com.decagon.avalanche.views.fragments

import android.app.ActivityOptions
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    lateinit var loggedOnSharePref: SharedPreferences

    override fun onStart() {
        super.onStart()

        /** Check loggedOn user status */
        checkIfUserLoggedOn()

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
        return binding.root
    }

    private fun checkIfUserLoggedOn() {

        loggedOnSharePref = requireActivity().getSharedPreferences("loggedOn", AppCompatActivity.MODE_PRIVATE)
        var isFirstTime: Boolean = loggedOnSharePref.getBoolean("firstTime", true)


        /** Check if user has not logged on before, else move to login screen */
        if (!isFirstTime) {
            findNavController().navigate(R.id.mainFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}