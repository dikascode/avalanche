package com.decagon.avalanche.firebase

import android.util.Log
import com.decagon.avalanche.data.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseProducts() {
    private var productsList = ArrayList<Product>()
    private var reference = FirebaseDatabase.getInstance().reference.child("Products")


    fun getProductsFromFirebase() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val product: Product? = dataSnapshot.getValue(Product::class.java)

                        //Log.d("TAG", "Productzzz: ${product?.title}")
                        if (product != null) {
                            productsList.add(product)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: $error")
            }

        })
    }

//    fun getFilteredProducts(title: String): List<Product> {
//        return getProductsFromFirebase().filter {
//            it.title.contains(title, true)
//        }
//    }

    fun getSingleProduct(title: String): Product {
        return productsList.first {
            it.title == title
        }
    }
}