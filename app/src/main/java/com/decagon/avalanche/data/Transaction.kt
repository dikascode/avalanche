package com.decagon.avalanche.data

data class Transaction (
    val ref: String,
    val amount:String,
    val ip: String,
    val status: String,
    val fraud_status: String,
    val fullName: String,
    val clientNumber: String,
    val email: String,
    val paymentType: String,
    val productsPurchased: String,
    val timeStamp: String

)