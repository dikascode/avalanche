package com.decagon.avalanche.data

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("name")
    val title: String,

    @SerializedName("photo_url")
    val photoUrl: String,

    val price: Double,

    @SerializedName("description")
    val desc: String,

    val isOnSale: Boolean
)