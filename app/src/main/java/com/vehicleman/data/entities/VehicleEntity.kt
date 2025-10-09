package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
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
    val registrationDate: Date,
    val oilChangeIntervalKm: Int,
    val oilChangeIntervalDays: Int
)