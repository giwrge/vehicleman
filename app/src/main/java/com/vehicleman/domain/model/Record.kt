package com.vehicleman.domain.model

import java.util.Date

/**
 * Domain Model for a Vehicle Maintenance Record (Expense or Reminder).
 * Αυτό αντικαθιστά τα παλιά δοκιμαστικά ExpenseEntity.
 */
data class MaintenanceRecord(
    val id: String,
    val vehicleId: String,
    val recordType: MaintenanceRecordType,
    val title: String,
    val description: String?,
    val date: Date, // Date of expense or reminder activation
    val odometer: Int,

    // Expense fields
    val cost: Double?,
    val quantity: Double?,
    val pricePerUnit: Double?,

    // Reminder fields
    val isReminder: Boolean,
    val reminderDate: Date?,
    val reminderOdometer: Int?,
    val isCompleted: Boolean = false
)

enum class MaintenanceRecordType {
    EXPENSE, // Κανονική Δαπάνη (π.χ. σέρβις, λάστιχα)
    REMINDER, // Απλή Υπενθύμιση (π.χ. ΚΤΕΟ, Ασφάλεια)
    FUEL_UP // Ειδικός τύπος για Ανεφοδιασμό (με λεπτομέρειες ποσότητας/τιμής)
}