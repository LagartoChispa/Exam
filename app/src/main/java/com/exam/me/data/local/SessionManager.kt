package com.exam.me.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN]
        }

    val userRole: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ROLE]
        }

    suspend fun saveSession(token: String, role: String) {
        context.dataStore.edit {
            it[PreferencesKeys.AUTH_TOKEN] = token
            it[PreferencesKeys.USER_ROLE] = role
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.clear()
        }
    }
}