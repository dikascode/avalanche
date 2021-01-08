package com.decagon.avalanche.firebase

import com.google.firebase.database.FirebaseDatabase

class FirebaseReference {

    companion object {
        val reference = FirebaseDatabase.getInstance().getReference("Products")

    }
}