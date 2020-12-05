package com.decagon.avalanche.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.decagon.avalanche.data.CartItem
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.network.RetroInstance
import com.decagon.avalanche.network.RetroService
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class AvalancheRepositoryImpl() : AvalancheRepository {
    lateinit var products: List<Product>

    private val retroInstance: RetroService =
        RetroInstance.getRetroInstance().create(RetroService::class.java)


    override suspend fun getAllProducts(): List<Product> {
        products = retroInstance.getAllProducts().body()!!
        return products
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