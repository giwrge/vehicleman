package com.vehicleman.presentation.addeditrecord

import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.use_case.record_ai.SuggestionItem
import java.util.Date

sealed class AddEditRecordEvent {

    data class LoadExisting(val recordId: String) : AddEditRecordEvent()

    data class TitleChanged(val value: String) : AddEditRecordEvent()
    data class DescriptionChanged(val value: String) : AddEditRecordEvent()
    data class DateTextChanged(val value: String) : AddEditRecordEvent()
    data class DateChanged(val value: Date) : AddEditRecordEvent()

    data class OdometerChanged(val value: String) : AddEditRecordEvent()
    data class CostChanged(val value: String) : AddEditRecordEvent()
    data class QuantityChanged(val value: String) : AddEditRecordEvent()
    data class PricePerUnitChanged(val value: String) : AddEditRecordEvent()
    data class FuelTypeChanged(val value: String) : AddEditRecordEvent()

    data class RecordTypeChanged(val value: RecordType) : AddEditRecordEvent()

    data class SuggestionClicked(val item: SuggestionItem) : AddEditRecordEvent()

    data class ReminderDateTextChanged(val value: String) : AddEditRecordEvent()
    data class ReminderDateChanged(val value: Date) : AddEditRecordEvent()
    data class ReminderOdometerChanged(val value: String) : AddEditRecordEvent()
    data class CostReminderChanged(val value: String) : AddEditRecordEvent()

    object ToggleCompleted : AddEditRecordEvent()
    data class ToggleReminder(val value: Boolean) : AddEditRecordEvent()

    object Save : AddEditRecordEvent()

    data class TranslateTitleConfirmation(val translate: Boolean, val dontAskAgain: Boolean) : AddEditRecordEvent()

    object NavigateBackConsumed : AddEditRecordEvent()
    object ErrorShown : AddEditRecordEvent()
}
