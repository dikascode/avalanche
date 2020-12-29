package com.decagon.avalanche.views.fragments

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
//            val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
//            pairs[0] = Pair<View, String>(binding.loginBtn, "transition_login")
//
//            val options =
//                ActivityOptions.makeSceneTransitionAnimation(requireActivity(), pairs)

            findNavController().navigate(R.id.loginFragment)
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}