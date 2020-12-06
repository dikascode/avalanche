package com.decagon.avalanche.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.decagon.avalanche.adapters.CartListAdapter
import com.decagon.avalanche.databinding.FragmentCartBinding
import com.decagon.avalanche.viewmodels.StoreViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeViewModel: StoreViewModel
    private lateinit var cartListAdapter: CartListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartListAdapter = CartListAdapter()
        binding.cartRv.adapter = cartListAdapter
        binding.cartRv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        storeViewModel.getCart()?.observe(viewLifecycleOwner, {
            if (it != null) {
                cartListAdapter.submitList(it)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}