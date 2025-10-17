package com.vehicleman.domain.repositories

import com.vehicleman.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Interface για το Repository Layer (Domain Layer).
 * Ορίζει τις λειτουργίες που πρέπει να παρέχει το Data Layer στην επιχειρηματική λογική.
 */
interface VehicleRepository {

    /** Επιστρέφει ένα Flow με όλα τα οχήματα. */
    fun getAllVehicles(): Flow<List<Vehicle>>

    /** Λαμβάνει ένα όχημα με βάση το ID του. */
    suspend fun getVehicleById(id: String): Vehicle?

    /** Αποθηκεύει (εισάγει ή ενημερώνει) ένα όχημα. */
    suspend fun saveVehicle(vehicle: Vehicle)

    suspend fun insertVehicle(vehicle: Vehicle)

    suspend fun updateVehicle(vehicle: Vehicle)

    /** Διαγράφει ένα όχημα. */
    suspend fun deleteVehicle(vehicle: Vehicle)

    /** ✅ ΝΕΑ ΛΕΙΤΟΥΡΓΙΑ: Διαγράφει όχημα με βάση το ID. */
    suspend fun deleteVehicleById(vehicleId: String)

    /** Διαγράφει μαζικά οχήματα με βάση ένα Set από IDs. */
    suspend fun deleteVehiclesByIds(vehicleIds: Set<String>)

    /** Επιστρέφει τον συνολικό αριθμό των οχημάτων. */
    suspend fun getVehicleCount(): Int
}
