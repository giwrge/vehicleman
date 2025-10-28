package com.vehicleman.presentation.addeditrecord

import java.util.Date

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
    data class OnFuelTypeSelected(val fuelType: String) : AddEditRecordEvent

    // Actions
    data object OnSaveClicked : AddEditRecordEvent
    data object OnTwoTapActivated : AddEditRecordEvent
}
