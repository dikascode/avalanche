package com.decagon.avalanche.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.decagon.avalanche.databinding.ActivityProductDetailsBinding
import com.decagon.avalanche.repository.ProductsRepository
import com.decagon.avalanche.viewmodels.ProductDetailsViewModel
import com.decagon.avalanche.viewmodels.ProductsListViewModel
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

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