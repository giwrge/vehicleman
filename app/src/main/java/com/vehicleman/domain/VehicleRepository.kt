package com.vehicleman.domain

import com.vehicleman.data.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface για το Αποθετήριο Οχημάτων.
 * Αυτό ορίζει τις λειτουργίες που είναι διαθέσιμες στο Domain/UI Layer,
 * απομονώνοντας την εφαρμογή από τις λεπτομέρειες της πηγής δεδομένων (Room).
 */
interface VehicleRepository {

    /**
     * Εισάγει ή ενημερώνει ένα όχημα.
     * @return Το row ID (Long) που αποθηκεύτηκε.
     */
    suspend fun saveVehicle(vehicle: VehicleEntity): Long

    /**
     * Διαγράφει ένα όχημα.
     */
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    /**
     * Διαγράφει πολλά οχήματα.
     */
    suspend fun deleteVehiclesByIds(vehicleIds: Set<String>)

    /**
     * Λαμβάνει όλα τα οχήματα και επιστρέφει ως Flow για ενημερώσεις σε πραγματικό χρόνο.
     */
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    /**
     * Λαμβάνει ένα όχημα με βάση το ID του.
     */
    suspend fun getVehicleById(vehicleId: String): VehicleEntity? // Νέα λειτουργία
}
