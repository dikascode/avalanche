package com.decagon.avalanche.preferencesdatastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(context: Context) {
    private  val dataStore = context.createDataStore("user_prefs")

    companion object {
        val USER_NAME_KEY = preferencesKey<String>("USER_FIRST_NAME")
        val USER_EMAIL_KEY = preferencesKey<String>("USER_EMAIL")
        val USER_PHONE_KEY = preferencesKey<String>("USER_PHONE")
    }

    suspend fun storeUser(name: String, email: String, phone: String) {
        dataStore.edit {
            it[USER_NAME_KEY] = name
            it[USER_EMAIL_KEY] = email
            it[USER_PHONE_KEY] = phone
        }
    }

    val userNameFlow: Flow<String> = dataStore.data.map {
        it[USER_NAME_KEY] ?: ""
    }

    val userEmailFlow: Flow<String> = dataStore.data.map {
        it[USER_EMAIL_KEY] ?: ""
    }

    val userPhoneFlow: Flow<String> = dataStore.data.map {
        it[USER_PHONE_KEY] ?: ""
    }
}