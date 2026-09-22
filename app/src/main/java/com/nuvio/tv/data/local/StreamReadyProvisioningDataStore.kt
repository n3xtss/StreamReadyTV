package com.nuvio.tv.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.streamReadyProvisioningDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "streamready_provisioning",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

@Singleton
class StreamReadyProvisioningDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.streamReadyProvisioningDataStore

    private val initializedKey = booleanPreferencesKey("initialized")
    private val lockedKey = booleanPreferencesKey("locked")

    val initialized: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[initializedKey] ?: false
    }

    val locked: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[lockedKey] ?: false
    }

    suspend fun markInitialized() {
        dataStore.edit { prefs ->
            prefs[initializedKey] = true
        }
    }

    suspend fun setLocked(locked: Boolean) {
        dataStore.edit { prefs ->
            prefs[lockedKey] = locked
        }
    }
}
