package com.decagon.avalanche.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.decagon.avalanche.R
import com.decagon.avalanche.cart.CartActivity
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.ui.fragments.AdminFragment
import com.decagon.avalanche.ui.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_main_fl, MainFragment())
                        .commit()
                }
                R.id.actionAdmin -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_main_fl, AdminFragment())
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

    //Open Navigation drawer on click of hamburger icon
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Prevent menu bar search icon from opening drawer
        if (item.itemId != R.id.action_search && item.itemId != R.id.action_cart) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }

        if (item.itemId == R.id.action_cart) {
            startActivity(Intent(this, CartActivity::class.java))
        }

        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }


}