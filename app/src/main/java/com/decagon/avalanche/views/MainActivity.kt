package com.decagon.avalanche.views


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.viewmodels.StoreViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var storeViewModel: StoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View binding instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        //get reference to root view
        val view = binding.root
        setContentView(view)

        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        storeViewModel.getCart()?.observe(this, Observer {
            if (it != null) {
                Log.d("TAG", "GetCartSize: ${it.size}")
            }
        })

        //Setup action bar with nav controller
//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)

        //Select only one item per time in Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actionHome -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
                }
                R.id.actionAdmin -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.adminFragment)
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
            findNavController(R.id.nav_host_fragment).navigate(R.id.cartFragment)
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }


}