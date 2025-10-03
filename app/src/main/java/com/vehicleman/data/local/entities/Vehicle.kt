// app/src/main/java/com/vehicleman/data/local/entities/Vehicle.kt

package com.vehicleman.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val vehicleId: Int = 0,
    val name: String, // π.χ. "My Toyota"
    val initialOdometer: Int, // Αρχικά χιλιόμετρα
    val fuelType: String, // π.χ. "Βενζίνη 95"
    val colorHex: String = "#17515F", // Χρώμα για το Card (UI/UX)
    val createdAt: Long = System.currentTimeMillis()
)