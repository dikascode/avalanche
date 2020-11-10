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
import com.decagon.avalanche.ui.fragments.AdminFragment
import com.decagon.avalanche.ui.fragments.MainFragment

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

        //Replace fragment layout with  Main fragment xml on launch of app
        supportFragmentManager.beginTransaction().replace(R.id.content_main_fl, MainFragment())
            .commit()

        //Select only one item per time in Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actionHome -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content_main_fl, MainFragment())
                        .commit()
                }
                R.id.actionAdmin -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content_main_fl, AdminFragment())
                        .commit()
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