package com.vehicleman.domain.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isNightMode: Flow<Boolean>
    suspend fun setNightMode(isNightMode: Boolean)
}
