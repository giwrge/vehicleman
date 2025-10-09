package com.vehicleman.presentation.records

import com.vehicleman.domain.model.MaintenanceRecordType
import java.util.Date

/**
 * State for the Add/Edit Maintenance Record screen (Intelligent Form).
 * (Το package entries μετονομάζεται σε records)
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
    val recordType: MaintenanceRecordType = MaintenanceRecordType.EXPENSE,
    val title: String = "",

    // Expense Details
    val cost: String = "",
    val quantity: String = "",
    val pricePerUnit: String = "",

    // Reminder Logic
    val isReminderSwitchLocked: Boolean = false,
    val isReminder: Boolean = false,
    val reminderDate: Date? = null,
    val reminderOdometer: String = "",

    // UI flags
    val showReminderFields: Boolean = false,
    val suggestions: List<String> = emptyList(),
    val showCostDetails: Boolean = false
)

sealed interface AddEditRecordEvent {
    // UI Interactions
    data class OnDescriptionChange(val text: String) : AddEditRecordEvent
    data class OnDateChange(val date: Date) : AddEditRecordEvent
    data class OnOdometerChange(val odometer: String) : AddEditRecordEvent
    data class OnCostChange(val cost: String) : AddEditRecordEvent
    data class OnQuantityChange(val quantity: String) : AddEditRecordEvent
    data class OnPricePerUnitChange(val price: String) : AddEditRecordEvent
    data class OnSuggestionChipClicked(val suggestion: String) : AddEditRecordEvent
    data class OnToggleReminder(val isChecked: Boolean) : AddEditRecordEvent
    data class OnReminderDateChange(val date: Date) : AddEditRecordEvent
    data class OnReminderOdometerChange(val odometer: String) : AddEditRecordEvent

    // Actions
    data object OnSaveClicked : AddEditRecordEvent
    data object OnTwoTapActivated : AddEditRecordEvent
}