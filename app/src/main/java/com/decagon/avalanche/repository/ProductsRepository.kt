package com.decagon.avalanche.repository

import com.decagon.avalanche.data.Product
import com.google.gson.Gson
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import java.net.URL

class ProductsRepository {
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
                product.title.contains(term, true) }
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
        val json = URL("https://finepointmobile.com/data/products.json").readText()
        return Gson().fromJson(json, Array<Product>::class.java).toList()
    }
}
