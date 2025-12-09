package com.ultimo.vehicleapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {
    companion object {
        private val SESSION_TOKEN = stringPreferencesKey("session_token")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_ADDRESS = stringPreferencesKey("user_address")
    }

    suspend fun saveSessionToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[SESSION_TOKEN] = token
        }
    }

    fun getSessionToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[SESSION_TOKEN]
        }
    }

    suspend fun saveRememberMe(remember: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = remember
        }
    }

    fun getRememberMe(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[REMEMBER_ME] ?: false
        }
    }

    // Synchronous version untuk check saat startup
    suspend fun getRememberMeSync(): Boolean {
        return context.dataStore.data.first()[REMEMBER_ME] ?: false
    }

    // Simpan user data
    suspend fun saveUserData(id: Int?, name: String?, email: String?, phone: String?, address: String?) {
        context.dataStore.edit { prefs ->
            id?.let { prefs[USER_ID] = it.toString() }
            name?.let { prefs[USER_NAME] = it }
            email?.let { prefs[USER_EMAIL] = it }
            phone?.let { prefs[USER_PHONE] = it }
            address?.let { prefs[USER_ADDRESS] = it }
        }
    }

    // Get user data sebagai Map
    suspend fun getUserData(): Map<String, String?> {
        val prefs = context.dataStore.data.first()
        return mapOf(
            "id" to prefs[USER_ID],
            "name" to prefs[USER_NAME],
            "email" to prefs[USER_EMAIL],
            "phone" to prefs[USER_PHONE],
            "address" to prefs[USER_ADDRESS]
        )
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_TOKEN)
        }
    }

    // Clear user data only
    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_PHONE)
            prefs.remove(USER_ADDRESS)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_TOKEN)
            prefs.remove(REMEMBER_ME)
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_PHONE)
            prefs.remove(USER_ADDRESS)
        }
    }
}

