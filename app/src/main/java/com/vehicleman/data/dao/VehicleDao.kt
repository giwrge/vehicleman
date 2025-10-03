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
 *
 * Παρέχει τις βασικές λειτουργίες CRUD (Create, Read, Update, Delete)
 * και χρησιμοποιεί Flow για παρακολούθηση αλλαγών σε πραγματικό χρόνο.
 */
@Dao
interface VehicleDao {

    /**
     * Εισάγει ένα νέο όχημα στη βάση. Εάν υπάρχει σύγκρουση, αντικαθιστά.
     * Επιστρέφει το row ID (Long).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long // Επιστρέφει Long

    /**
     * Ενημερώνει ένα υπάρχον όχημα.
     */
    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    /**
     * Διαγράφει ένα όχημα.
     */
    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    /**
     * Λαμβάνει όλα τα οχήματα από τη βάση δεδομένων.
     * Επιστρέφει Flow<List<VehicleEntity>> για παρακολούθηση αλλαγών.
     */
    @Query("SELECT * FROM vehicles ORDER BY registrationDate DESC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    /**
     * Λαμβάνει ένα όχημα με βάση το ID του (String).
     */
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: String): VehicleEntity? // String ID

    /**
     * Διαγράφει πολλαπλά οχήματα με βάση τα IDs τους.
     */
    @Query("DELETE FROM vehicles WHERE id IN (:vehicleIds)")
    suspend fun deleteVehiclesByIds(vehicleIds: Set<String>)
}
