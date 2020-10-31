package com.decagon.avalanche.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.ActivityProductDetailsBinding

class ProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater  )
        val view = binding.root
        setContentView(view)

        binding.productTitleTv.text = intent.getStringExtra("title")
    }
}