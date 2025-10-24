package com.vehicleman.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vehicleman.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val IS_NIGHT_MODE = booleanPreferencesKey("is_night_mode")
    }

    override val isNightMode = context.dataStore.data.map {
        it[PreferencesKeys.IS_NIGHT_MODE] ?: false
    }

    override suspend fun setNightMode(isNightMode: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_NIGHT_MODE] = isNightMode
        }
    }
}