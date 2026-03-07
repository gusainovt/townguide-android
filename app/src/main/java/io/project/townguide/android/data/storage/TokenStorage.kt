package io.project.townguide.android.data.storage


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenStorage(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("jwt_refresh_token")
    }

    val token: Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    val refreshToken: Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun saveTokens(token: String, refreshToken: String?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            if (refreshToken.isNullOrBlank()) {
                prefs.remove(REFRESH_TOKEN_KEY)
            } else {
                prefs[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
