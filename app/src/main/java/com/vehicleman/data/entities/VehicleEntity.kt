package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Αναπαριστά ένα όχημα στη βάση δεδομένων (Room Entity).
 * Περιλαμβάνει όλα τα απαραίτητα πεδία, συμπεριλαμβανομένης της ημερομηνίας εγγραφής.
 */
@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val fuelType: String,
    val initialOdometer: Int,
    val registrationDate: Long = System.currentTimeMillis()
)
