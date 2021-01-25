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
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.decagon.avalanche.R
import com.decagon.avalanche.adapters.CartListAdapter
import com.decagon.avalanche.data.CartItem
import com.decagon.avalanche.data.Transaction
import com.decagon.avalanche.databinding.FragmentCartBinding
import com.decagon.avalanche.firebase.FirebaseReference
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
    lateinit var userManager: com.decagon.avalanche.preferencesdatastore.UserManager

    var productTitleList = arrayListOf<String>()

    //Store userdata from datastore
    var userData = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        userManager = com.decagon.avalanche.preferencesdatastore.UserManager(requireActivity())
        userManager.userEmailFlow.asLiveData().observe(requireActivity(), { email ->
            userData.add(email)
        })

        userManager.userPhoneFlow.asLiveData().observe(requireActivity(), { phone ->
            userData.add(phone)
        })

        userManager.userFNameFlow.asLiveData().observe(requireActivity(), { fname ->
            userData.add(fname)
        })

        userManager.userLNameFlow.asLiveData().observe(requireActivity(), { lname ->
            userData.add(lname)
        })

        Log.i("TAG", "onCreateView: $userData")

        storeViewModel.getCart()?.observe(viewLifecycleOwner, {
            if (it != null) {
                cartListAdapter.submitList(it)
                binding.cartSubmitBtn.isEnabled = it.isNotEmpty()

                for (item in it) {
                    productTitleList.add(item.product.title)
                }
            }
        })

        storeViewModel.getTotalPrice()?.observe(viewLifecycleOwner, {
            binding.price.text = it.toString()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartListAdapter = CartListAdapter(this)
        binding.cartRv.adapter = cartListAdapter
        binding.cartRv.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        binding.cartSubmitBtn.setOnClickListener {
            makePayment()
            storeViewModel.resetCart()
            productTitleList.clear()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        productTitleList.clear()
        _binding = null
    }

    override fun deleteItem(cartItem: CartItem?) {
        storeViewModel.removeItemFromCart(cartItem)
        productTitleList.remove(cartItem!!.product.title)
    }

    override fun changeQuantity(cartItem: CartItem?, quantity: Int) {
        storeViewModel.changeQuantity(cartItem, quantity)
    }

    private fun makePayment() {
        val totalPrice = binding.price.text.toString()
        RaveUiManager(this)
            .setAmount(totalPrice.toDouble())
            .setEmail(userData[0])
            .setfName(userData[2])
            .setlName(userData[3])
            .setNarration("Purchase of cloths from Avalanche")
            .setCurrency("NGN")
            .setPublicKey("FLWPUBK_TEST-6921d097ab745d1e299bccf98fbc7ac1-X")
            .setEncryptionKey("FLWSECK_TESTb5408b7e58ee")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .setPhoneNumber(userData[1], true)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .onStagingEnv(true)
            .shouldDisplayFee(true)
            .showStagingLabel(true)
            .initialize()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var productTitles = productTitleList.joinToString()
        Log.i("TAG", "product titles: $productTitles")

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message: String? = data.getStringExtra("response").toString()
            Log.d("TAG", "response: $message")

            //Parse JSON from
            if (message != "null") {
                val parsedObject = JSONObject(message)
                val transactionResponse = parsedObject.getJSONObject("data")


                when (resultCode) {
                    RavePayActivity.RESULT_SUCCESS -> {
                        val amount = transactionResponse.get("amount")
                        val tranxRef = transactionResponse.get("txRef")

                        Toast.makeText(requireActivity(), "SUCCESS", Toast.LENGTH_LONG).show()
                        Log.d("Successful Transaction", "Transaction amount: $amount")
                        Log.d(
                            "Successful Transaction",
                            "Transaction IP: ${transactionResponse.get("IP")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction status: ${transactionResponse.get("status")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction fraud status: ${transactionResponse.get("fraud_status")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction ref: ${transactionResponse.get("txRef")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction customer name: ${transactionResponse.get("customer.fullName")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction customer email: ${transactionResponse.get("customer.email")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction customer phone: ${transactionResponse.get("customer.phone")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction payment type: ${transactionResponse.get("paymentType")}"
                        )
                        Log.d(
                            "Successful Transaction",
                            "Transaction msg: ${transactionResponse.get("vbvrespmessage")}"
                        )

                        val reference = FirebaseReference.transactionRef
                        val newTransaction = Transaction(tranxRef.toString(),
                            amount.toString(),
                            transactionResponse.get("IP").toString(),
                            transactionResponse.get("status").toString(),
                            transactionResponse.get("fraud_status").toString(),
                            transactionResponse.get("customer.fullName").toString(),
                            transactionResponse.get("customer.phone").toString(),
                            transactionResponse.get("customer.email").toString(),
                            transactionResponse.get("paymentType").toString(),
                            productTitles
                        )

                        reference.child(tranxRef.toString()).setValue(newTransaction)
                            .addOnSuccessListener {
                                // Write was successful!
                                makeToast("Transaction saved to database successfully")

                            }
                            .addOnFailureListener { error ->
                                // Write failed
                                Log.i("TAG", "transactionFailed: ${error.message}")
                                makeToast("Transaction not added successfully.")
                            }

                        findNavController().navigate(R.id.orderFragment)
                    }
                    RavePayActivity.RESULT_ERROR -> {
                        Toast.makeText(requireActivity(), "ERROR", Toast.LENGTH_LONG).show()
                        Log.d(
                            "Failed Transaction",
                            "Transaction status: ${transactionResponse.get("status")}"
                        )
                        Log.d(
                            "Failed Transaction",
                            "Transaction msg: ${transactionResponse.get("vbvrespmessage")}"
                        )

                        findNavController().navigate(R.id.failedTransactionFragment)
                    }
                    RavePayActivity.RESULT_CANCELLED -> {
                        Toast.makeText(requireActivity(), "CANCELLED", Toast.LENGTH_LONG).show()
                        Log.d(
                            "Failed Transaction",
                            "Transaction status: ${transactionResponse.get("status")}"
                        )
                        Log.d(
                            "Failed Transaction",
                            "Transaction msg: ${transactionResponse.get("vbvrespmessage")}"
                        )

                        findNavController().navigate(R.id.mainFragment)
                    }
                }
            }

        } else {
            //Redirect to failed page
        }
    }

    override fun onPause() {
        super.onPause()

        var productTitles = productTitleList.joinToString()
        Log.i("TAG", "product titles: $productTitles")
    }


    private fun makeToast(str: String) {
        Toast.makeText(requireActivity(),
            str,
            Toast.LENGTH_LONG).show()
    }
}