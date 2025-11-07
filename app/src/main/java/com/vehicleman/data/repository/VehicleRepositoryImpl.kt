package com.vehicleman.data.repository

import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.data.mappers.toVehicle
import com.vehicleman.data.mappers.toVehicleEntity
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του VehicleRepository (Data Layer).
 * Συνδέει τα DAO Entities με τα Domain Models.
 */
@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getAllVehicles().map { list -> list.map(VehicleEntity::toVehicle) }
    }

    override suspend fun getVehicleById(id: String): Vehicle? {
        return vehicleDao.getVehicleById(id)?.toVehicle()
    }

    override suspend fun saveVehicle(vehicle: Vehicle) {
        val existing = vehicleDao.getVehicleById(vehicle.id)
        if (existing == null) {
            vehicleDao.insertVehicle(vehicle.toVehicleEntity())
        } else {
            vehicleDao.updateVehicle(vehicle.toVehicleEntity())
        }
    }

    override suspend fun insertVehicle(vehicle: Vehicle) {
        vehicleDao.insertVehicle(vehicle.toVehicleEntity())
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.updateVehicle(vehicle.toVehicleEntity())
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.deleteVehicle(vehicle.toVehicleEntity())
    }

    override suspend fun deleteVehicleById(vehicleId: String) {
        vehicleDao.deleteVehicleById(vehicleId)
    }

    override suspend fun deleteVehiclesByIds(vehicleIds: Set<String>) {
        vehicleDao.deleteVehiclesByIds(vehicleIds.toList())
    }

    override suspend fun getVehicleCount(): Int {
        return vehicleDao.getVehicleCount()
    }

    override suspend fun getAllVehiclesList(): List<Vehicle> {
        return vehicleDao.getAllVehiclesList().map { it.toVehicle() }
    }

    override suspend fun deleteAllVehicles() {
        vehicleDao.deleteAllVehicles()
    }

    override suspend fun insertAllVehicles(vehicles: List<Vehicle>) {
        vehicles.forEach { vehicleDao.insertVehicle(it.toVehicleEntity()) }
    }
}