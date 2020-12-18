package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentFailedTransactionBinding
import com.decagon.avalanche.databinding.FragmentOrderBinding
import com.decagon.avalanche.viewmodels.StoreViewModel

class FailedTransactionFragment : Fragment() {
    private var _binding:FragmentFailedTransactionBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFailedTransactionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view = binding.root

        binding.continueShoppingBtn.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}