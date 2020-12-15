package com.decagon.avalanche.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.decagon.avalanche.R
import com.decagon.avalanche.adapters.CartListAdapter
import com.decagon.avalanche.data.CartItem
import com.decagon.avalanche.databinding.FragmentCartBinding
import com.decagon.avalanche.viewmodels.StoreViewModel
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import org.json.JSONObject


class CartFragment : Fragment(), CartListAdapter.CartInterface {
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

        binding.cartSubmitBtn.setOnClickListener {
            makePayment()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartListAdapter = CartListAdapter(this)
        binding.cartRv.adapter = cartListAdapter
        binding.cartRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        storeViewModel.getCart()?.observe(viewLifecycleOwner, {
            if (it != null) {
                cartListAdapter.submitList(it)
                binding.cartSubmitBtn.isEnabled = it.isNotEmpty()
            }
        })

        storeViewModel.getTotalPrice()?.observe(viewLifecycleOwner, {
            binding.price.text = it.toString()
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteItem(cartItem: CartItem?) {
        storeViewModel.removeItemFromCart(cartItem)
    }

    override fun changeQuantity(cartItem: CartItem?, quantity: Int) {
        storeViewModel.changeQuantity(cartItem, quantity)
    }

    private fun makePayment() {
        val totalPrice = binding.price.text.toString()
        RaveUiManager(this)
            .setAmount(totalPrice.toDouble())
            .setEmail("lexypoet@gmail.com")
            .setfName("Dika")
            .setlName("Kyle")
            .setNarration("Purchase of cloths from Avalanche")
            .setCurrency("NGN")
            .setPublicKey("FLWPUBK_TEST-6921d097ab745d1e299bccf98fbc7ac1-X")
            .setEncryptionKey("FLWSECK_TESTb5408b7e58ee")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .setPhoneNumber("08135081549", true)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .onStagingEnv(true)
            .shouldDisplayFee(true)
            .showStagingLabel(true)
            .initialize()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message: String? = data.getStringExtra("response")
            Log.d("TAG", "response: $message")

            //Parse JSON from
            val parsedObject = JSONObject(message)
            val transactionResponse = parsedObject.getJSONObject("data")

            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    val amount = transactionResponse.get("amount")

                    Toast.makeText(requireContext(), "SUCCESS", Toast.LENGTH_LONG).show()
                    Log.d("Successful Transaction", "Transaction amount: $amount")
                    Log.d("Successful Transaction", "Transaction IP: ${transactionResponse.get("IP")}")
                    Log.d("Successful Transaction", "Transaction status: ${transactionResponse.get("status")}")
                    Log.d("Successful Transaction", "Transaction fraud status: ${transactionResponse.get("fraud_status")}")
                    Log.d("Successful Transaction", "Transaction ref: ${transactionResponse.get("txRef")}")
                    Log.d("Successful Transaction", "Transaction customer name: ${transactionResponse.get("customer.fullName")}")
                    Log.d("Successful Transaction", "Transaction customer email: ${transactionResponse.get("customer.email")}")
                    Log.d("Successful Transaction", "Transaction customer phone: ${transactionResponse.get("customer.phone")}")
                    Log.d("Successful Transaction", "Transaction payment type: ${transactionResponse.get("paymentType")}")
                    Log.d("Successful Transaction", "Transaction msg: ${transactionResponse.get("vbvrespmessage")}")


                    findNavController().navigate(R.id.mainFragment)
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
                    Log.d("Failed Transaction", "Transaction status: ${transactionResponse.get("status")}")
                    Log.d("Failed Transaction", "Transaction msg: ${transactionResponse.get("vbvrespmessage")}")
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(requireContext(), "CANCELLED", Toast.LENGTH_LONG).show()
                    Log.d("Failed Transaction", "Transaction status: ${transactionResponse.get("status")}")
                    Log.d("Failed Transaction", "Transaction msg: ${transactionResponse.get("vbvrespmessage")}")
                }
            }
        } else {
            //Redirect to failed page
        }
    }
}