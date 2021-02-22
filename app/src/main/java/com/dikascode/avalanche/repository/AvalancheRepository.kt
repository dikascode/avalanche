package com.dikascode.avalanche.repository

import com.dikascode.avalanche.data.Product

interface AvalancheRepository {
  fun getAllProducts(): List<Product>

    suspend fun searchForProducts(term: String): List<Product>

    suspend fun getProductByName(title: String): Product
}