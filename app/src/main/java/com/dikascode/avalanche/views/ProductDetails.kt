package com.dikascode.avalanche.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.dikascode.avalanche.databinding.ActivityProductDetailsBinding
import com.dikascode.avalanche.viewmodels.ProductDetailsViewModel
import com.dikascode.avalanche.viewmodels.StoreViewModel
import com.squareup.picasso.Picasso

class ProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var storeViewModel: StoreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productDetailsViewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)
        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        val title = intent.getStringExtra("title")

        if (title != null) {
            productDetailsViewModel.getProductByName(title)
        }

        productDetailsViewModel.productDetailsLiveData.observe(this, {
            binding.productTitleTv.text = it.title
            binding.productPriceTv.text = it.price.toString()
            binding.productDescTv.text = it.desc
            val photoUrl = it.photoUrl
            Picasso.get().load(photoUrl).into(binding.productImageIv)

            binding.addToCartBtn.setOnClickListener { _ ->
                val isAdded: Boolean = storeViewModel.addProductToCart(it)

                Log.d("TAG", "addItemOnProductDetail: ${it.title} is $isAdded")
            }
        })

    }
}