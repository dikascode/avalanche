package com.decagon.avalanche.views


import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.decagon.avalanche.NetworkStatusChecker
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.viewmodels.StoreViewModel
import com.rommansabbir.networkx.NetworkX
import com.rommansabbir.networkx.NetworkXObservingStrategy
import com.rommansabbir.networkx.isInternetConnectedLiveData


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var storeViewModel: StoreViewModel

    private var cartQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        //get reference to root view
        val view = binding.root
        setContentView(view)

        val networkConnection = NetworkStatusChecker(this)

        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                binding.noInternetView.noInternetIv.visibility = View.INVISIBLE

                storeViewModel.getCart()?.observe(this, {
                    if (it != null) {
                        var quantity = 0
                        for (cartItem in it) {
                            quantity += cartItem.quantity
                        }

                        cartQuantity = quantity

                        //Redraw menu
                        invalidateOptionsMenu()
                    }
                })

                //Select only one item per time in Navigation Drawer
                binding.navigationView.setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.actionHome -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
                        }
                        R.id.actionAdmin -> {
//                            findNavController(R.id.nav_host_fragment).navigate(R.id.adminFragment)
                            finish()
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

            } else {
                Toast.makeText(
                    this,
                    "Oops. Please check your internet connection and try again",
                    Toast.LENGTH_LONG
                ).show()

                storeViewModel.getCart()?.removeObservers(this)
                binding.noInternetView.noInternetIv.visibility = View.VISIBLE

            }
        })


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
        val menuItem = menu?.findItem(R.id.action_cart)
        val actionView: View = menuItem!!.actionView
        val cartBadgeTV: TextView = actionView.findViewById(R.id.cart_badge_text)

        cartBadgeTV.text = cartQuantity.toString()

        if (cartQuantity < 1) {
            cartBadgeTV.visibility = View.GONE
        }

        //Make cart clickable
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
        return true
    }


//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.clear()
//
//        val menuItem = menu?.findItem(R.id.action_cart)
//        val actionView: View = menuItem!!.actionView
//        val cartBadgeTV: TextView = actionView.findViewById(R.id.cart_badge_text)
//
//        cartBadgeTV.text = cartQuantity.toString()
//
//        if(cartQuantity < 1){
//            cartBadgeTV.visibility = View.GONE
//        }
//
//        return super.onPrepareOptionsMenu(menu)
//    }


}