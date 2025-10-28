package com.vehicleman.domain.model

import java.util.Date

data class Record(

    val id: String,
    val vehicleId: String,
    val recordType: RecordType,
    val title: String,
    val description: String?,
    val date: Date, // Date of expense or reminder activation
    val odometer: Int,

    // Expense fields
    val cost: Double?,
    val quantity: Double?,
    val pricePerUnit: Double?,
    val fuelType: String? = null, // To store the specific fuel type for a FUEL_UP

    // Reminder fields
    val isReminder: Boolean,
    val reminderDate: Date?,
    val reminderOdometer: Int?,
    val isCompleted: Boolean = false
)
enum class RecordType {
    EXPENSE, // Κανονική Δαπάνη (π.χ. σέρβις, λάστιχα)
    REMINDER, // Απλή Υπενθύμιση (π.χ. ΚΤΕΟ, Ασφάλεια)
    FUEL_UP // Ειδικός τύπος για Ανεφοδιασμό (με λεπτομέρειες ποσότητας/τιμής)
}