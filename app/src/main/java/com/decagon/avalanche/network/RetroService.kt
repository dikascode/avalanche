package com.decagon.avalanche.network

import androidx.lifecycle.LiveData
import com.decagon.avalanche.data.Product
import retrofit2.Response
import retrofit2.http.GET

interface RetroService {
//   " data/products.json"
    @GET("api/ecommerce/v1/allProducts")
    suspend fun getAllProducts(): Response<List<Product>>
}