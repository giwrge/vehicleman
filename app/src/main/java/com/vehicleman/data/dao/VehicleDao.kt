package com.vehicleman.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vehicleman.data.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) για αλληλεπίδραση με τον πίνακα 'vehicles'.
 */
@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles ORDER BY reg_date DESC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    /**
     * Λαμβάνει ένα όχημα με βάση το ID του.
     */
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: String): VehicleEntity?

    /**
     * Νέα Query για τον έλεγχο ορίου οχημάτων.
     */
    @Query("SELECT COUNT(id) FROM vehicles")
    suspend fun getVehicleCount(): Int

    /**
     * ΝΕΑ ΛΕΙΤΟΥΡΓΙΑ: Διαγράφει πολλαπλά οχήματα με βάση τη λίστα των IDs τους.
     * Χρησιμοποιεί την εντολή IN της SQL.
     */
    @Query("DELETE FROM vehicles WHERE id IN (:vehicleIds)")
    suspend fun deleteVehiclesByIds(vehicleIds: List<String>)
}
