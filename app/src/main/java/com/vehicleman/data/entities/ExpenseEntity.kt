
package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,
    val vehicleId: Int,
    val recordedByUserId: Int? = null, // FOREIGN KEY στο User (PRO Feature)

    val entryDate: Long,
    val entryOdometer: Int,
    val category: String, // π.χ. "Fuel", "Service", "Taxes"
    val amount: Double,

    val rawEntryText: String,

    val isReminder: Boolean = false,
    val reminderType: String? = null,
    val reminderOdometer: Int? = null,

    val eventIconName: String, // π.χ. "fuel_pump", "wrench"
    val lineStyle: String, // π.χ. "Double_Solid", "Single_Dashed_Left"
    val displayOrder: Long = entryDate
)