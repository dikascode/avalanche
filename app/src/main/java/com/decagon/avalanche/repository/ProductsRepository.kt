package com.decagon.avalanche.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.decagon.avalanche.data.Product
import com.google.firebase.database.*
import com.google.gson.Gson
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import java.net.URL

class ProductsRepository(val context: Context) {
    private lateinit var reference:DatabaseReference
    private lateinit var productsList: ArrayList<Product>
    //Repository using RxJava
    fun getAllProducts(): @NonNull Single<List<Product>>? {
        return Single.create<List<Product>> {
            val products = fetchProducts()
            it.onSuccess(products)
        }
    }

    fun searchForProducts(term: String): Single<List<Product>> {
        return Single.create {
            val filteredProduct = fetchProducts().filter { product ->
                product.title.contains(term, true)
            }
            it.onSuccess(filteredProduct)
        }
    }

    fun getProductByName(title: String): Single<Product> {
        return Single.create {
            val product = fetchProducts().first { it.title == title }
            it.onSuccess(product)
        }
    }

    fun getProductPhotos() {

    }

    private fun fetchProducts(): List<Product> {
        reference = FirebaseDatabase.getInstance().reference.child("Products")


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products: Product? = snapshot.getValue(Product::class.java)
                if (products != null) {
                    productsList.add(products)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Something went wrong: $error", Toast.LENGTH_LONG)
                Log.d("TAG", "onCancelled: $error")
            }

        })
//        val json = URL("https://finepointmobile.com/data/products.json").readText()
//        return Gson().fromJson(json, Array<Product>::class.java).toList()

        return productsList
    }
}
