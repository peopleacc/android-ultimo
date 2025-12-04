package com.ultimo.vehicleapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {
    companion object {
        private val SESSION_TOKEN = stringPreferencesKey("session_token")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
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

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_TOKEN)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_TOKEN)
            prefs.remove(REMEMBER_ME)
        }
    }
}
