package com.vehicleman.domain.repositories

import com.vehicleman.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getAllVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: String): Vehicle?
    suspend fun saveVehicle(vehicle: Vehicle)
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
    suspend fun deleteVehicleById(vehicleId: String)
    suspend fun deleteVehiclesByIds(vehicleIds: Set<String>)
    suspend fun getVehicleCount(): Int
    
    // For Backup/Restore
    suspend fun getAllVehiclesList(): List<Vehicle>
    suspend fun deleteAllVehicles()
    suspend fun insertAllVehicles(vehicles: List<Vehicle>)
}