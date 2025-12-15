package com.vehicleman.presentation.addeditrecord

import com.vehicleman.domain.model.RecordType
import java.util.Date

sealed class AddEditRecordEvent {

    // --- Initialization ---
    data class LoadExisting(val recordId: String) : AddEditRecordEvent()

    // --- Core fields ---
    data class TitleChanged(val value: String) : AddEditRecordEvent()
    data class DescriptionChanged(val value: String) : AddEditRecordEvent()
    data class DateTextChanged(val value: String) : AddEditRecordEvent()
    data class DateChanged(val value: Date) : AddEditRecordEvent()

    data class OdometerChanged(val value: String) : AddEditRecordEvent()
    data class CostChanged(val value: String) : AddEditRecordEvent()
    data class QuantityChanged(val value: String) : AddEditRecordEvent()
    data class PricePerUnitChanged(val value: String) : AddEditRecordEvent()
    data class FuelTypeChanged(val value: String) : AddEditRecordEvent()

    // --- Record type switching ---
    data class RecordTypeChanged(val value: RecordType) : AddEditRecordEvent()

    // --- Suggestions ---
    data class SuggestionClicked(val suggestion: String) : AddEditRecordEvent()

    // --- Reminder fields ---
    data class ReminderDateTextChanged(val value: String) : AddEditRecordEvent()
    data class ReminderDateChanged(val value: Date) : AddEditRecordEvent()
    data class ReminderOdometerChanged(val value: String) : AddEditRecordEvent()

    data class CostReminderChanged(val value: String) : AddEditRecordEvent()
    object ToggleCompleted : AddEditRecordEvent()

    // Switch ON/OFF (but locked if auto-generated)
    data class ToggleReminder(val value: Boolean) : AddEditRecordEvent()

    // --- Save ---
    object Save : AddEditRecordEvent()

    // --- Navigation / Errors ---
    object NavigateBackConsumed : AddEditRecordEvent()
    object ErrorShown : AddEditRecordEvent()
}
