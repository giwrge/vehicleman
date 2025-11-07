package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val name: String, // Added name field
    val make: String,
    val model: String,
    val plateNumber: String,
    val year: Int,
    val fuelTypes: List<String>, // Changed from fuelType: String
    val currentOdometer: Int,
    val registrationDate: Date?,
    val oilChangeDate: Long,
    val oilChangeKm: Long,
    val tiresChangeKm: Long,
    val tiresChangeDate: Long,
    val insuranceExpiryDate: Long,
    val taxesExpiryDate: Long,
    val dateAdded: Long,
    val lastModified: Long,
    val recordCount: Int
)
