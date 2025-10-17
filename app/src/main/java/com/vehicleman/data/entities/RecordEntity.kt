package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Defines the structure for the 'entries' table (Expenses/Reminders) in the Room database.
 */
@Entity(tableName = "entries")
data class RecordEntity(
    @PrimaryKey
    val id: String,
    val vehicleId: String, // Ξένο κλειδί για σύνδεση με το όχημα
    val entryType: String, // 'EXPENSE' ή 'REMINDER'
    val title: String, // Περιγραφή ή τίτλος υπενθύμισης
    val description: String?,
    val date: Date, // Ημερομηνία συμβάντος/δαπάνης
    val odometer: Int, // Χιλιόμετρα

    // Πεδία Δαπανών
    val cost: Double?,
    val quantity: Double?, // π.χ., λίτρα βενζίνης
    val pricePerUnit: Double?, // π.χ., τιμή λίτρου

    // Πεδία Υπενθυμίσεων
    val isReminder: Boolean,
    val reminderDate: Date?,
    val reminderOdometer: Int?,
    val isCompleted: Boolean = false // Για υπενθυμίσεις
)