package com.vehicleman.domain.repositories

import com.vehicleman.data.entities.DriverWithVehicles
import com.vehicleman.domain.model.Driver
import com.vehicleman.data.entities.VehicleDriverCrossRef
import kotlinx.coroutines.flow.Flow

interface DriverRepository {
    fun getDriversWithVehicles(): Flow<List<DriverWithVehicles>>
    suspend fun addDriver(name: String)
    suspend fun deleteDriver(driverId: String)
    suspend fun assignVehicleToDriver(driverId: String, vehicleId: String)
    suspend fun unassignVehicleFromDriver(driverId: String, vehicleId: String)
    
    // For Backup/Restore
    suspend fun getAllDriversList(): List<Driver>
    suspend fun getAllVehicleDriverCrossRefs(): List<VehicleDriverCrossRef>
    suspend fun deleteAllDrivers()
    suspend fun deleteAllCrossRefs()
    suspend fun insertAllDrivers(drivers: List<Driver>)
    suspend fun insertAllCrossRefs(crossRefs: List<VehicleDriverCrossRef>)
}