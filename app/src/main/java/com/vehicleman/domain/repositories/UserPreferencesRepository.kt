package com.vehicleman.domain.repositories

import kotlinx.coroutines.flow.Flow

enum class VehicleSortOrder {
    ALPHABETICAL,
    BY_DATE_ADDED,
    MOST_ENTRIES,
    BY_LAST_MODIFIED,
    CUSTOM
}

enum class UserStatus {
    FREE,
    SIGNED_UP
}

enum class ProLevel {
    NONE, // Default level for FREE and SIGNED_UP users
    PRO_1,
    PRO_2,
    PRO_3
}

enum class TwinAppRole {
    NONE,
    MAIN_DRIVER,
    SUB_DRIVER
}

enum class SubDriverType {
    FULL,
    SINGLE
}

data class User(
    val status: UserStatus = UserStatus.FREE,
    val proLevel: ProLevel = ProLevel.NONE,
    val twinAppRole: TwinAppRole = TwinAppRole.NONE,
    val subDriverType: SubDriverType? = null, // Null if not a SUB_DRIVER
    val assignedVehicleIds: List<String> = emptyList(), // For SingleSubDrivers
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val country: String = "",
    val email: String = "",
)

interface UserPreferencesRepository {
    val isNightMode: Flow<Boolean>
    suspend fun setNightMode(isNightMode: Boolean)

    val vehicleSortOrder: Flow<VehicleSortOrder>
    suspend fun setVehicleSortOrder(sortOrder: VehicleSortOrder)

    val customVehicleOrder: Flow<String>
    suspend fun setCustomVehicleOrder(order: String)
    
    val user: Flow<User>
    suspend fun saveUser(user: User)
    
    val recordCreationCount: Flow<Int>
    suspend fun incrementRecordCreationCount()
}