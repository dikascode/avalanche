package com.decagon.avalanche.network


import com.decagon.avalanche.constants.Constants.Companion.CONTENT_TYPE
import com.decagon.avalanche.constants.Constants.Companion.SERVER_KEY
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroService {
//   " data/products.json"
//    @GET("api/ecommerce/v1/allProducts")
//    suspend fun getAllProducts(): Response<List<Product>>

    @Headers("Authorization: key=${SERVER_KEY}", "Content-Type:${CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ):Response<ResponseBody>
}