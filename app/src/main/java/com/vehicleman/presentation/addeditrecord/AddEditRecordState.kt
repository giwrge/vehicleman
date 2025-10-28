package com.vehicleman.presentation.addeditrecord

import com.vehicleman.domain.model.RecordType
import java.util.Date

/**
 * State for the Add/Edit  Record screen (Intelligent Form).
 */
data class AddEditRecordState(
    val recordId: String = "", // Αναφέρεται στο ID του Συμβάντος
    val vehicleId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    // Form Inputs
    val date: Date = Date(),
    val odometer: String = "",
    val description: String = "", // Smart Input

    // Analysis/Type Output
    val recordType: RecordType = RecordType.EXPENSE,
    val title: String = "",

    // Expense Details
    val cost: String = "",
    val quantity: String = "",
    val pricePerUnit: String = "",

    // Fuel Type Selection
    val vehicleFuelTypes: List<String> = emptyList(),
    val selectedFuelType: String? = null,
    val showFuelTypeSelection: Boolean = false,

    // Reminder Logic
    val isReminderSwitchLocked: Boolean = false,
    val isReminder: Boolean = false,
    val reminderDate: Date? = null,
    val reminderOdometer: String = "",

    // UI flags
    val showReminderFields: Boolean = false,
    val suggestions: List<String> = emptyList(),
    val showCostDetails: Boolean = false,

    val isSaveSuccess: Boolean = false

)
