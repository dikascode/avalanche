package com.decagon.avalanche.preferencesdatastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserManager(context: Context) {
    private val dataStore = context.createDataStore("user_prefs")
    private val rmDataStore = context.createDataStore("rm_prefs")

    companion object {
        val USER_NAME_KEY = preferencesKey<String>("USER_FIRST_NAME")
        val USER_EMAIL_KEY = preferencesKey<String>("USER_EMAIL")
        val USER_PHONE_KEY = preferencesKey<String>("USER_PHONE")
        val USER_PASSWORD = preferencesKey<String>("USER_PASSWORD")
        val USER_cCODE = preferencesKey<String>("USER_cCODE")
    }

    suspend fun storeUser(name: String, email: String, phone: String) {
        dataStore.edit {
            it[USER_NAME_KEY] = name
            it[USER_EMAIL_KEY] = email
            it[USER_PHONE_KEY] = phone
        }

    }


    suspend fun createRememberMeSession(phone: String, password: String, cCode: String) {
        rmDataStore.edit {
            it[USER_PASSWORD] = password
            it[USER_PHONE_KEY] = phone
            it[USER_cCODE] = cCode
        }
    }

    val userNameFlow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
            it[USER_NAME_KEY] ?: ""
        }

    val userEmailFlow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is java.io.IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_EMAIL_KEY] ?: ""
    }

    val userPhoneFlow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_PHONE_KEY] ?: ""
    }


    /**
     * Remember me sessions
     */
    val rmUserPhoneFlow: Flow<String> = rmDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_PHONE_KEY] ?: ""
    }


    val rmCountryCodeFlow: Flow<String> = rmDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_cCODE] ?: ""
    }


    val rmUserPasswordFlow: Flow<String> = rmDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_PASSWORD] ?: ""
    }
}