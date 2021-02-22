package com.dikascode.avalanche.preferencesdatastore

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
        val USER_FNAME_KEY = preferencesKey<String>("USER_FIRST_NAME")
        val USER_LNAME_KEY = preferencesKey<String>("USER_LAST_NAME")
        val USER_EMAIL_KEY = preferencesKey<String>("USER_EMAIL")
        val USER_PHONE_KEY = preferencesKey<String>("USER_PHONE")
        val USER_PASSWORD = preferencesKey<String>("USER_PASSWORD")
        val USER_cCODE = preferencesKey<String>("USER_cCODE")
        val ADMIN_STATUS = preferencesKey<Boolean>("ADMIN_STATUS")
    }

    suspend fun storeUser(fname: String, lname:String, email: String, phone: String, adminStatus: Boolean) {
        dataStore.edit {
            it[USER_FNAME_KEY] = fname
            it[USER_LNAME_KEY] = lname
            it[USER_EMAIL_KEY] = email
            it[USER_PHONE_KEY] = phone
            it[ADMIN_STATUS] = adminStatus
        }

    }


    suspend fun createRememberMeSession(phone: String, password: String, cCode: String) {
        rmDataStore.edit {
            it[USER_PASSWORD] = password
            it[USER_PHONE_KEY] = phone
            it[USER_cCODE] = cCode
        }
    }

    val userFNameFlow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_FNAME_KEY] ?: ""
    }

    val userLNameFlow: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[USER_LNAME_KEY] ?: ""
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

    val userAdminFlow: Flow<Boolean> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[ADMIN_STATUS] ?: false
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