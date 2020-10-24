package com.decagon.avalanche

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.model.Product

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        //get reference to root view
        val view = binding.root
        setContentView(view)

        val products = arrayListOf<Product>()

        for(i in 0..10) products.add(Product("Palaso Trouser", "https://picsum.photos/id/1037/200", 4, 1000))
        recyclerView = findViewById(R.id.content_main_rv)
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = ProductsAdapter(products)
        }
    }
}