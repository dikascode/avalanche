package com.decagon.avalanche.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.FragmentOrderBinding
import com.decagon.avalanche.viewmodels.StoreViewModel

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    val binding get() = _binding!!

    lateinit var storeViewModel: StoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view = binding.root

        val storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

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