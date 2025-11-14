package com.vehicleman.presentation.record

sealed class RecordEvent {
    data class VehicleSelected(val vehicleId: String) : RecordEvent()
    object Refresh : RecordEvent()
    data class ToggleExpandRecord(val recordId: String) : RecordEvent()
    data class MarkReminderCompleted(val recordId: String) : RecordEvent()
    data class DeleteRecord(val recordId: String) : RecordEvent()
    data class NavigateToEdit(val recordId: String?) : RecordEvent()
}
