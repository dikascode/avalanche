package com.decagon.avalanche.repository

import androidx.lifecycle.LiveData
import com.decagon.avalanche.data.Product

interface AvalancheRepository {
    suspend fun getAllProducts(): List<Product>

    suspend fun searchForProducts(term: String): List<Product>

    suspend fun getProductByName(title: String): Product
}