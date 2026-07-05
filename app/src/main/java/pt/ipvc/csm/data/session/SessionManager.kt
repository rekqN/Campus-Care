package pt.ipvc.csm.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "csm_session")

/**
 * Persists which user is currently logged in, so the session survives app restarts.
 * Backed by Jetpack DataStore.
 */
class SessionManager(private val context: Context) {

    val currentUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    suspend fun setLoggedInUser(userId: Long) {
        context.dataStore.edit { it[KEY_USER_ID] = userId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_USER_ID) }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "pt"
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = code }
    }

    companion object {
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }
}
