package com.vehicleman.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vehicleman.data.entities.DriverEntity
import com.vehicleman.data.entities.DriverWithVehicles
import com.vehicleman.data.entities.VehicleDriverCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Query("DELETE FROM drivers WHERE driverId = :driverId")
    suspend fun deleteDriver(driverId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun assignVehicleToDriver(crossRef: VehicleDriverCrossRef)

    @Query("DELETE FROM VehicleDriverCrossRef WHERE driverId = :driverId AND id = :vehicleId")
    suspend fun unassignVehicleFromDriver(driverId: String, vehicleId: String)

    @Transaction
    @Query("SELECT * FROM drivers")
    fun getDriversWithVehicles(): Flow<List<DriverWithVehicles>>
    
    @Query("SELECT * FROM drivers")
    suspend fun getAllDriversList(): List<DriverEntity>

    @Query("SELECT * FROM VehicleDriverCrossRef")
    suspend fun getAllVehicleDriverCrossRefs(): List<VehicleDriverCrossRef>
    
    @Query("DELETE FROM drivers")
    suspend fun deleteAllDrivers()

    @Query("DELETE FROM VehicleDriverCrossRef")
    suspend fun deleteAllCrossRefs()
}