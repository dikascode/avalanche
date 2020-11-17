package com.decagon.avalanche

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.decagon.avalanche.adapters.ProductsAdapter
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.room.RoomProductModel
import com.decagon.avalanche.ui.ProductDetails
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


//Get all products from repository api call


//ProductsRepository().getAllProducts()?.let { recyclerViewGridLayout(it) }

//make network call on background thread
//        val thread = Thread {
//            try {
//                //Your code goes here
//
//                val productsFromDatabase = ProductModel(db.productDao()).readAllProducts()
//
//                val products = mapProductListFromDatabaseQuery(productsFromDatabase)
//
//                //Update ui on UI thread
//                requireActivity().runOnUiThread(Runnable {
//                    // Update recyclerview UI
//                    recyclerViewGridLayout(products)
//                })
//
//                db.close()
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        thread.start()



private fun mapProductListFromDatabaseQuery(productsFromDatabase: List<RoomProductModel>): List<Product> {
    return productsFromDatabase.map {
        Product(
            it.title, "https://finepointmobile.com/data/jeans2.jpg", it.price, it.desc,  true
        )
    }
}


//private fun recyclerViewGridLayout(productsRepository: Single<List<Product>>) {
//    val recyclerView = binding.fragmentMainRv
//
//    productsRepository?.subscribeOn(Schedulers.io())
//        ?.observeOn(AndroidSchedulers.mainThread())?.subscribe({
//            recyclerView.apply {
//                layoutManager = GridLayoutManager(activity, 2)
//                adapter =
//                    ProductsAdapter(it as ArrayList<Product>) { extraTitle, extraImageUrl, photoView ->
//
//                        //Go to product details when image is clicked
//                        val intent = Intent(activity, ProductDetails::class.java)
//                        intent.putExtra("title", extraTitle)
//                        intent.putExtra("photo_url", extraImageUrl)
//
//                        //Shared elements transition animations
//                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            activity as AppCompatActivity,
//                            photoView,
//                            "photoToAnimate"
//                        )
//                        startActivity(intent, options.toBundle())
//                    }
//            }
//        }, {
//            Log.d("Log", "onSuccess: ${it.message}")
//        })
//
//    //Make progress bar invisible after UI has been updated
//    binding.fragmentMainProgressBar.visibility = View.GONE
//}
