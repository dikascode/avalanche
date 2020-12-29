package com.decagon.avalanche.data

data class User (
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val admin: Boolean

)