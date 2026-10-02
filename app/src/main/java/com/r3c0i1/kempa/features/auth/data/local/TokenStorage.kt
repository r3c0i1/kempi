package com.r3c0i1.kempa.features.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "kempi_auth_store")

class TokenStorage(
    private val context: Context
) {

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.authDataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
        }
    }

    val accessTokenFlow: Flow<String?> = context.authDataStore.data
        .catch { exception ->
            if(exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }

    val refreshTokenFlow: Flow<String?> = context.authDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN]
        }

    suspend fun getAccessToken(): String? = accessTokenFlow.firstOrNull()
    suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()
    suspend fun hasToken(): Boolean = !getAccessToken().isNullOrBlank()
}