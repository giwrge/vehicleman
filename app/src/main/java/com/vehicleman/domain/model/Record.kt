package com.vehicleman.domain.model

import java.util.Date

data class Record(
    val id: String,
    val vehicleId: String,
    val recordType: RecordType,
    val category: RecordExpenseCategory? = null,
    val title: String,
    val description: String?,
    val date: Date,
    val odometer: Int,

    // Expense fields
    val cost: Double?,
    val quantity: Double?,
    val pricePerUnit: Double?,
    val fuelType: String? = null,
    val isFullTank: Boolean = false, // ✅ NEW

    // Reminder fields
    val isReminder: Boolean,
    val reminderDate: Date?,
    val reminderOdometer: Int?,
    val isCompleted: Boolean = false,
    val costReminder: Double? = null
)

enum class RecordType {
    EXPENSE,
    REMINDER,
    FUEL_UP
}

// Μετονομασία για αποφυγή σύγκρουσης με το sealed class RecordCategory.ExpenseCategory
enum class RecordExpenseCategory {
    FUEL,
    SERVICE,
    TIRES,
    TAXES,
    INSURANCE,
    REPAIRS,
    CLEANING,
    PARKING,
    OTHER
}
