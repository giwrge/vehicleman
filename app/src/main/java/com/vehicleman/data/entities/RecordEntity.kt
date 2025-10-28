package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "records") // Όρισε το όνομα του πίνακα
data class RecordEntity(
    @PrimaryKey
    val id: String,
    val vehicleId: String,

    val recordType: String, // "EXPENSE", "REMINDER", "FUEL_UP" - Πολύ σημαντικό!
    val title: String,
    val description: String?, // Καλύτερα nullable αν μπορεί να λείπει

    val date: Date,
    val odometer: Int,

    // Πεδία που αφορούν κυρίως τα Expenses
    val cost: Double?, // Nullable γιατί τα Reminders δεν έχουν κόστος
    val quantity: Double?, // Nullable, αφορά μόνο τα Fuel-ups
    val pricePerUnit: Double?, // Nullable, αφορά μόνο τα Fuel-ups
    val fuelType: String?, // To store the specific fuel type for a FUEL_UP

    // Πεδία που αφορούν τα Reminders
    val isReminder: Boolean,
    val reminderDate: Date?, // Nullable
    val reminderOdometer: Int?, // Nullable

    val isCompleted: Boolean // π.χ. για να μαρκάρεις ένα reminder ως ολοκληρωμένο
)
