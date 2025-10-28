package com.vehicleman.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val gson = Gson()

    private object PreferencesKeys {
        val IS_NIGHT_MODE = booleanPreferencesKey("is_night_mode")
        val VEHICLE_SORT_ORDER = stringPreferencesKey("vehicle_sort_order")
        val CUSTOM_VEHICLE_ORDER = stringPreferencesKey("custom_vehicle_order")
        val USER_DATA = stringPreferencesKey("user_data")
        val RECORD_CREATION_COUNT = intPreferencesKey("record_creation_count")
    }

    override val isNightMode = context.dataStore.data.map {
        it[PreferencesKeys.IS_NIGHT_MODE] ?: false
    }

    override suspend fun setNightMode(isNightMode: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_NIGHT_MODE] = isNightMode
        }
    }

    override val vehicleSortOrder = context.dataStore.data.map { preferences ->
        VehicleSortOrder.valueOf(
            preferences[PreferencesKeys.VEHICLE_SORT_ORDER] ?: VehicleSortOrder.ALPHABETICAL.name
        )
    }

    override suspend fun setVehicleSortOrder(sortOrder: VehicleSortOrder) {
        context.dataStore.edit {
            it[PreferencesKeys.VEHICLE_SORT_ORDER] = sortOrder.name
        }
    }

    override val customVehicleOrder: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.CUSTOM_VEHICLE_ORDER] ?: ""
    }

    override suspend fun setCustomVehicleOrder(order: String) {
        context.dataStore.edit {
            it[PreferencesKeys.CUSTOM_VEHICLE_ORDER] = order
        }
    }

    override val user: Flow<User> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.USER_DATA]
        if (json != null) {
            gson.fromJson(json, User::class.java)
        } else {
            User() // Default user
        }
    }

    override suspend fun saveUser(user: User) {
        context.dataStore.edit {
            val json = gson.toJson(user)
            it[PreferencesKeys.USER_DATA] = json
        }
    }

    override val recordCreationCount: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.RECORD_CREATION_COUNT] ?: 0
    }

    override suspend fun incrementRecordCreationCount() {
        context.dataStore.edit {
            val currentCount = it[PreferencesKeys.RECORD_CREATION_COUNT] ?: 0
            it[PreferencesKeys.RECORD_CREATION_COUNT] = currentCount + 1
        }
    }
}