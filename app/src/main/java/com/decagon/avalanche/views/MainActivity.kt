package com.decagon.avalanche.views


import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.decagon.avalanche.NetworkStatusChecker
import com.decagon.avalanche.R
import com.decagon.avalanche.databinding.ActivityMainBinding
import com.decagon.avalanche.viewmodels.StoreViewModel
import com.google.firebase.messaging.FirebaseMessaging

const val TOPIC = "/topics/product"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private lateinit var storeViewModel: StoreViewModel
    private var userManager = com.decagon.avalanche.preferencesdatastore.UserManager(this)

    var isAdmin = false
    private var cartQuantity = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager.userAdminFlow.asLiveData().observe(this, { admin ->
            isAdmin = admin
            if (isAdmin)
                binding.navigationView.menu.findItem(R.id.actionAdmin).isVisible = true
        })

        /**
         * Subscribe to push notification topic
         */
        FirebaseMessaging.getInstance().subscribeToTopic("product")

        /**
         * Hide admin drawer menu option
         */
        binding.navigationView.menu.findItem(R.id.actionAdmin).isVisible = false

        val networkConnection = NetworkStatusChecker(this)

        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        checkNetworkStatus(networkConnection)


    }



    private fun checkNetworkStatus(networkConnection: NetworkStatusChecker) {
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
                            if (isAdmin) {
                                findNavController(R.id.nav_host_fragment).navigate(R.id.adminFragment)
                            }
                        }

                        R.id.actionContact -> {
                            val installed: Boolean = whatsappInstalledOrNot("com.whatsapp")

                            if (installed) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data =
                                    Uri.parse("http://api.whatsapp.com/send?phone=+2348165264168&text=" + "Hello Avalanche")
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Whatsapp not installed on this device. Please install Whatsapp.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        R.id.actionLogOut -> {
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

    private fun whatsappInstalledOrNot(url: String): Boolean {
        val packageManager = packageManager

        return try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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

}