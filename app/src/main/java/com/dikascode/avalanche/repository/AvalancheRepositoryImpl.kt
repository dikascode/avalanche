package com.dikascode.avalanche.repository

import android.util.Log
import com.dikascode.avalanche.data.Product
import com.google.firebase.database.*

class AvalancheRepositoryImpl() : AvalancheRepository {
    var productsList = ArrayList<Product>()

//    private val retroInstance: RetroService =
//        RetroInstance.getRetroInstance().create(RetroService::class.java)

   private var reference = FirebaseDatabase.getInstance().reference.child("Products")


    override fun getAllProducts(): List<Product> {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(dataSnapshot in snapshot.children) {
                        val product: Product? = dataSnapshot.getValue(Product::class.java)

                        Log.d("TAG", "Products: ${product?.title}")
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

        //productsList = retroInstance.getAllProducts().body()!!
        return productsList
    }

    override suspend fun searchForProducts(term: String): List<Product> {
        return getAllProducts().filter {
            it.title.contains(term, true)
        }
    }

    override suspend fun getProductByName(title: String): Product {
        return getAllProducts().first {
            it.title == title
        }
    }
}