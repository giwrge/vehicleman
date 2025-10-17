package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val plate: String,
    val year: Int,
    val fuelType: String,
    val odometer: Int,
    val registrationDate: Date,
    val oilChangeTime: String,
    val oilChangeKm: Int,
    val tiresChangeTime: String,
    val tiresChangeKm: Int,
    val insurancePaymentDate: String,
    val taxesPaymentDate: String
)
