package com.dikascode.avalanche.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.dikascode.avalanche.R
import com.dikascode.avalanche.adapters.CartListAdapter
import com.dikascode.avalanche.api.JavaMailApi
import com.dikascode.avalanche.data.CartItem
import com.dikascode.avalanche.data.Transaction
import com.dikascode.avalanche.databinding.FragmentCartBinding
import com.dikascode.avalanche.firebase.FirebaseReference
import com.dikascode.avalanche.utils.showToast
import com.dikascode.avalanche.viewmodels.StoreViewModel
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import org.json.JSONObject


class CartFragment : Fragment(), CartListAdapter.CartInterface {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeViewModel: StoreViewModel
    private lateinit var cartListAdapter: CartListAdapter
    lateinit var userManager: com.dikascode.avalanche.preferencesdatastore.UserManager

    var productTitleList = arrayListOf<String>()

    var title = ""

    //Store userdata from datastore
    var userData = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        storeViewModel = ViewModelProvider(requireActivity()).get(StoreViewModel::class.java)

        userManager = com.dikascode.avalanche.preferencesdatastore.UserManager(requireActivity())
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
            //val price = formatter?.format(it)
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
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

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
            .setfName(userData[0])
            .setlName(userData[1] + " " + userData[3] + " " + userData[2])
            .setNarration("Purchase of clothes from Avalanche")
            .setCurrency("NGN")
            .setPublicKey("FLWPUBK-716de398e434ea87112ce1d1b84f1f30-X")
            .setEncryptionKey("94c29dd8354ea616dfcba212")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .acceptCardPayments(true)
            .onStagingEnv(false)
            .shouldDisplayFee(false)
            .allowSaveCardFeature(true)
            .showStagingLabel(true)
            .initialize()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var productTitles = productTitleList.toSet().joinToString()
        Log.i("TAG", "activity result titles: $productTitles")

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

                        showToast("SUCCESSFUL", requireActivity())

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
                            productTitles,
                            transactionResponse.get("customer.updatedAt").toString()
                        )

                        reference.child(tranxRef.toString()).setValue(newTransaction)
                            .addOnSuccessListener {
                                // Write was successful!
                                showToast("Transaction saved successfully", requireActivity())

                                val customerMailMessage =
                                    "Avalanche has successfully received your order for: $productTitles\n" +
                                            "Total price: N$amount.\n" +
                                            "You will be contacted soon to have your delivery within three days."

                                val avalancheMailMessage =
                                    "You have an order from ${transactionResponse.get("customer.fullName")}\n " +
                                            "Total Price: N$amount\n " +
                                            "Items Purchased: $productTitles"

                                //Retrieve user saved email
                                val emailSharedPref =
                                    requireActivity().getSharedPreferences("userEmail",
                                        Context.MODE_PRIVATE).getString("email", null)

                                //Send mail to customer
                                sendMail(emailSharedPref!!,
                                    "Avalanche Transaction Receipt",
                                    customerMailMessage)

                                /**Send mail to avalanche */
//                                sendMail("northwrite19@gmail.com",
//                                    "You have a new Order",
//                                    avalancheMailMessage)

                                sendMail("dutchezglintz@gmail.com",
                                    "You have a new Order",
                                    avalancheMailMessage)

                                productTitleList.clear()
                                findNavController().navigate(R.id.orderFragment)
                            }
                            .addOnFailureListener { error ->
                                // Write failed
                                Log.i("TAG", "transactionFailed: ${error.message}")
                                showToast("Transaction not saved successfully.", requireActivity())

                                findNavController().navigate(R.id.failedTransactionFragment)
                                productTitleList.clear()
                            }

                    }
                    RavePayActivity.RESULT_ERROR -> {
                        showToast("ERROR", requireActivity())
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
                        showToast("CANCELLED", requireActivity())
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

    override fun onDestroyView() {
        super.onDestroyView()
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