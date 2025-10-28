package com.vehicleman.data.repository

import com.vehicleman.data.dao.DriverDao
import com.vehicleman.data.entities.DriverEntity
import com.vehicleman.data.entities.DriverWithVehicles
import com.vehicleman.data.entities.VehicleDriverCrossRef
import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.repositories.DriverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val driverDao: DriverDao
) : DriverRepository {

    override fun getDriversWithVehicles(): Flow<List<DriverWithVehicles>> {
        return driverDao.getDriversWithVehicles()
    }

    override suspend fun addDriver(name: String) {
        driverDao.insertDriver(DriverEntity(name = name))
    }

    override suspend fun deleteDriver(driverId: String) {
        driverDao.deleteDriver(driverId)
    }

    override suspend fun assignVehicleToDriver(driverId: String, vehicleId: String) {
        driverDao.assignVehicleToDriver(VehicleDriverCrossRef(driverId = driverId, id = vehicleId))
    }

    override suspend fun unassignVehicleFromDriver(driverId: String, vehicleId: String) {
        driverDao.unassignVehicleFromDriver(driverId, vehicleId)
    }

    override suspend fun getAllDriversList(): List<Driver> {
        return driverDao.getAllDriversList().map { Driver(it.driverId, it.name) }
    }

    override suspend fun getAllVehicleDriverCrossRefs(): List<VehicleDriverCrossRef> {
        return driverDao.getAllVehicleDriverCrossRefs()
    }

    override suspend fun deleteAllDrivers() {
        driverDao.deleteAllDrivers()
    }

    override suspend fun deleteAllCrossRefs() {
        driverDao.deleteAllCrossRefs()
    }

    override suspend fun insertAllDrivers(drivers: List<Driver>) {
        drivers.forEach { driverDao.insertDriver(DriverEntity(it.driverId, it.name)) }
    }

    override suspend fun insertAllCrossRefs(crossRefs: List<VehicleDriverCrossRef>) {
        crossRefs.forEach { driverDao.assignVehicleToDriver(it) }
    }
}