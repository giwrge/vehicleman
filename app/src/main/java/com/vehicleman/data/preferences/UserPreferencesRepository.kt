package com.vehicleman.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val SHOW_AUTO_REMINDERS = booleanPreferencesKey("show_auto_reminders")
    }

    val showAutoRemindersFlow: Flow<Boolean> = context.dataStore.data
        .map {
            it[PreferencesKeys.SHOW_AUTO_REMINDERS] ?: true // Default to true
        }

    suspend fun setShowAutoReminders(show: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.SHOW_AUTO_REMINDERS] = show
        }
    }
}
