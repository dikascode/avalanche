package com.decagon.avalanche.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.decagon.avalanche.databinding.ActivityProductDetailsBinding
import com.decagon.avalanche.viewmodels.ProductDetailsViewModel
import com.squareup.picasso.Picasso

class ProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    lateinit var viewModel: ProductDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater  )
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)
        val title = intent.getStringExtra("title")

        if (title != null) {
            viewModel.getProductByName(title)
        }

        viewModel.productDetailsLiveData.observe(this, {
            binding.productTitleTv.text = it.title
            binding.productPriceTv.text = it.price.toString()
            binding.productDescTv.text = it.desc
            val photoUrl = it.photoUrl
            Picasso.get().load(photoUrl).into(binding.productImageIv)
        })

    }
}