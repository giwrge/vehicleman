package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecordEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val date: Long,
    val odometer: Int,
    val isExpense: Boolean, // True for expense, false for reminder
    val title: String,
    val description: String?,
    val amount: Double?,
    val reminderDate: Long?
)
