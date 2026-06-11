package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey
    val id: String,
    val vehicleId: String,

    val recordType: String, // "EXPENSE", "REMINDER", "FUEL_UP"
    val category: String?,  // "FUEL", "SERVICE", "TIRES", etc.
    val title: String,
    val description: String?,

    val date: Date,
    val odometer: Int,

    // Expense / Fuel fields
    val cost: Double?,
    val quantity: Double?,
    val pricePerUnit: Double?,
    val fuelType: String?,
    val isFullTank: Boolean, // ✅ NEW

    // Reminder fields
    val isReminder: Boolean,
    val reminderDate: Date?,
    val reminderOdometer: Int?,
    val costReminder: Double?,          // ✅ NEW (nullable)
    val isCompleted: Boolean
)
