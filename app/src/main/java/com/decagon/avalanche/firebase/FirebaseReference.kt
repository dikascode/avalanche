package com.decagon.avalanche.firebase

import com.google.firebase.database.FirebaseDatabase

class FirebaseReference {

    companion object {
        val productReference = FirebaseDatabase.getInstance().getReference("Products")
        val userReference = FirebaseDatabase.getInstance().getReference("Users")
        val transactionRef = FirebaseDatabase.getInstance().getReference("Transactions")

    }
}