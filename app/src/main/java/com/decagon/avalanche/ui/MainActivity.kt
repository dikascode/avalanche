package com.decagon.avalanche.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.adapter.ProductsAdapter
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.model.Product
import kotlin.math.log

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

        for(i in 0..10) products.add(Product("Lady Gown $i", "https://picsum.photos/id/1037/200", 4, 1000))
        recyclerView = findViewById(R.id.content_main_rv)
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = ProductsAdapter(products)
        }


        //Select only one item per time in Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.actionHome -> {
                    d("action", "Home was pressed")
                }
            }
            it.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }


        //Setup hamburger for navigation drawer
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu_icon)

        }



    }
    
    //Open Navigation drawer on click of icon
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        return true
    }
}