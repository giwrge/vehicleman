package com.vehicleman.presentation.addeditrecord

import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import java.util.Date

data class AddEditRecordState(

    // --- Loading / Navigation ---
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val navigateBack: Boolean = false,
    val errorMessage: String? = null,

    // --- Identification ---
    val vehicleId: String = "",
    val recordId: String? = null,

    // --- Core fields ---
    val recordType: RecordType = RecordType.EXPENSE,
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val dateText: String = "",

    // These fields must MATCH the ViewModel + Screen
    val odometer: String = "",
    val cost: String = "",
    val quantity: String = "",
    val pricePerUnit: String = "",

    // Fuel
    val fuelType: String? = null,
    val fuelTypeText: String = "",

    // --- Reminders ---
    val isReminder: Boolean = false,
    val isCompleted: Boolean = false,
    val reminderDate: Date? = null,
    val reminderDateText: String = "",
    val reminderOdometer: String = "",
    val isReminderSwitchLocked: Boolean = false,
    val showReminderFields: Boolean = false,

    // --- Dynamic UI logic ---
    val showCostDetails: Boolean = false,
    val showFuelTypeSelection: Boolean = false,

    // --- Suggestions (chips) ---
    val suggestions: List<String> = emptyList(),

    // --- Vehicle-specific fuel types ---
    val vehicleFuelTypes: List<String> = emptyList(),
    val selectedFuelType: String? = null,

    // --- Categorizer output ---
    val category: RecordCategory? = null,

    // --- Save button ---
    val isSaveEnabled: Boolean = false
)
