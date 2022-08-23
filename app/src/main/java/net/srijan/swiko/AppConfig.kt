package net.srijan.swiko

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class AppConfig(private val dataStore: DataStore<Preferences>){

    private object PrefKeys {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    val userToken = dataStore
        .data
        .map { pref ->
            pref[PrefKeys.USER_TOKEN]
        }

    suspend fun setUserToken(token: String) {
        dataStore.edit { pref ->
            pref[PrefKeys.USER_TOKEN] = token
        }
    }

}