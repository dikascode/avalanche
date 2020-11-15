package com.decagon.avalanche.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.ActivityProductDetailsBinding
import com.decagon.avalanche.repository.ProductsRepository
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater  )
        val view = binding.root
        setContentView(view)

        val title = intent.getStringExtra("title")
        ProductsRepository().getProductByName(title!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.productTitleTv.text = it.title
                binding.productPriceTv.text = it.price.toString()
                binding.productDescTv.text = it.desc
                val photoUrl = it.photoUrl
                Picasso.get().load(photoUrl).into(binding.productImageIv)
            }, { Log.d("Failure", "msg: ${ it.message }")})


    }
}