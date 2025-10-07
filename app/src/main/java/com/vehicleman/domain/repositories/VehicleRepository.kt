package com.vehicleman.domain.repositories

import com.vehicleman.domain.model.Vehicle // ΔΙΟΡΘΩΣΗ: Αλλαγή από .models σε .model
import kotlinx.coroutines.flow.Flow

/**
 * Interface για το Repository Layer (Domain Layer).
 * Ορίζει τις λειτουργίες που πρέπει να παρέχει το Data Layer στην επιχειρηματική λογική.
 *
 * Σημαντικό: Χρησιμοποιεί μόνο το Domain Model (Vehicle) για να διατηρήσει την
 * ανεξαρτησία του Domain Layer από το Data Layer (π.χ. Room Entities).
 */
interface VehicleRepository {

    /**
     * Επιστρέφει ένα Flow με όλα τα οχήματα (Domain Models).
     */
    fun getAllVehicles(): Flow<List<Vehicle>>

    /**
     * Λαμβάνει ένα όχημα (Domain Model) με βάση το ID του.
     */
    suspend fun getVehicleById(id: String): Vehicle?

    /**
     * Αποθηκεύει (εισάγει ή ενημερώνει) ένα όχημα (Domain Model).
     * Επιστρέφει Unit, καθώς το Domain Layer δεν ενδιαφέρεται για το Row ID της βάσης.
     */
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    /**
     * Διαγράφει ένα μόνο όχημα.
     */
    suspend fun deleteVehicle(vehicle: Vehicle)

    /**
     * Διαγράφει μαζικά οχήματα με βάση ένα Set από IDs.
     */
    suspend fun deleteVehiclesByIds(vehicleIds: Set<String>)

    /**
     * Επιστρέφει τον συνολικό αριθμό των καταχωρημένων οχημάτων.
     */
    suspend fun getVehicleCount(): Int
}
