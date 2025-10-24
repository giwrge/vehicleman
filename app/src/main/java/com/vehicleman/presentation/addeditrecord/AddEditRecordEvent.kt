package com.vehicleman.presentation.addeditrecord

sealed class AddEditRecordEvent {
    data class OnDateChanged(val date: Long) : AddEditRecordEvent()
    data class OnOdometerChanged(val odometer: String) : AddEditRecordEvent()
    data class OnIsExpenseChanged(val isExpense: Boolean) : AddEditRecordEvent()
    data class OnTitleChanged(val title: String) : AddEditRecordEvent()
    data class OnDescriptionChanged(val description: String) : AddEditRecordEvent()
    data class OnAmountChanged(val amount: String) : AddEditRecordEvent()
    data class OnReminderDateChanged(val reminderDate: Long?) : AddEditRecordEvent()
    object OnSaveRecord : AddEditRecordEvent()
}
