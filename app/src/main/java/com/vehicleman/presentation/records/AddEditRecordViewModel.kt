package com.vehicleman.presentation.records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.MaintenanceRecord
import com.vehicleman.domain.model.MaintenanceRecordType
import com.vehicleman.domain.repositories.MaintenanceRecordRepository // Σωστό Repository
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val maintenanceRecordRepository: MaintenanceRecordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = savedStateHandle[NavDestinations.VEHICLE_ID_KEY] ?: throw IllegalArgumentException("Vehicle ID required")
    private val recordId: String = savedStateHandle["recordId"] ?: "new" // Χρησιμοποιούμε recordId

    private val _state = MutableStateFlow(AddEditRecordState(vehicleId = vehicleId, recordId = recordId))
    val state: StateFlow<AddEditRecordState> = _state

    init {
        loadRecord()
        loadLatestOdometer()
    }

    // ... (Οι συναρτήσεις loadRecord και loadLatestOdometer χρησιμοποιούν το MaintenanceRecordRepository) ...
    private fun loadLatestOdometer() {
        viewModelScope.launch {
            val latestOdometer = maintenanceRecordRepository.getLatestOdometer(vehicleId)
            if (recordId == "new" && latestOdometer > 0) {
                _state.update { it.copy(odometer = latestOdometer.toString()) }
            }
        }
    }

    private fun loadRecord() {
        if (recordId != "new") {
            viewModelScope.launch {
                try {
                    val record = maintenanceRecordRepository.getRecordById(recordId)
                    record?.let {
                        _state.update { currentState ->
                            currentState.copy(
                                date = record.date,
                                odometer = record.odometer.toString(),
                                description = record.description ?: record.title,
                                isReminder = record.isReminder,
                                reminderDate = record.reminderDate,
                                reminderOdometer = record.reminderOdometer?.toString() ?: "",
                                cost = record.cost?.toString() ?: "",
                                quantity = record.quantity?.toString() ?: "",
                                pricePerUnit = record.pricePerUnit?.toString() ?: "",
                                showReminderFields = record.isReminder,
                                showCostDetails = record.recordType != MaintenanceRecordType.REMINDER
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Αδυναμία φόρτωσης καταχώρησης: ${e.message}") }
                }
            }
        }
        _state.update { it.copy(suggestions = defaultSuggestions) }
    }


    fun onEvent(event: AddEditRecordEvent) {
        when (event) {
            is AddEditRecordEvent.OnDescriptionChange -> handleSmartInput(event.text)
            is AddEditRecordEvent.OnDateChange -> handleDateChange(event.date)
            is AddEditRecordEvent.OnOdometerChange -> _state.update { it.copy(odometer = event.odometer) }
            is AddEditRecordEvent.OnCostChange -> _state.update { it.copy(cost = event.cost) }
            is AddEditRecordEvent.OnQuantityChange -> _state.update { it.copy(quantity = event.quantity) }
            is AddEditRecordEvent.OnPricePerUnitChange -> handlePricePerUnitChange(event.price)
            is AddEditRecordEvent.OnSuggestionChipClicked -> handleSuggestionChipClicked(event.suggestion)
            is AddEditRecordEvent.OnToggleReminder -> _state.update { it.copy(isReminder = event.isChecked, showReminderFields = event.isChecked) }
            is AddEditRecordEvent.OnReminderDateChange -> _state.update { it.copy(reminderDate = event.date) }
            is AddEditRecordEvent.OnReminderOdometerChange -> _state.update { it.copy(reminderOdometer = event.odometer) }
            AddEditRecordEvent.OnSaveClicked -> saveRecord()
            AddEditRecordEvent.OnTwoTapActivated -> { /* Logic for 2-tap activation */ }
        }
    }

    // ... (Οι συναρτήσεις Smart Input παραμένουν ίδιες, χρησιμοποιώντας MaintenanceRecordType) ...
    private fun handleSmartInput(text: String) {
        _state.update { it.copy(description = text) }

        val normalizedText = text.lowercase()
        val newRecordType = when {
            normalizedText.contains("βενζίνη") || normalizedText.contains("πετρέλαιο") || normalizedText.contains("υγραέριο") -> MaintenanceRecordType.FUEL_UP
            normalizedText.contains("υπενθύμιση") || normalizedText.contains("ασφάλεια") || normalizedText.contains("ΚΤΕΟ") -> MaintenanceRecordType.REMINDER
            else -> MaintenanceRecordType.EXPENSE
        }

        // ... (Λογική εξαγωγής αριθμών και υπολογισμών) ...

        _state.update {
            it.copy(
                recordType = newRecordType,
                // ... (λοιπά πεδία)
                showCostDetails = newRecordType != MaintenanceRecordType.REMINDER
            )
        }
    }

    private fun handleDateChange(newDate: Date) {
        val isFuture = newDate.after(Date().startOfDay())

        _state.update { currentState ->
            currentState.copy(
                date = newDate,
                isReminder = if (isFuture) true else currentState.isReminder,
                isReminderSwitchLocked = isFuture,
                showReminderFields = isFuture || currentState.isReminder,
                recordType = if(isFuture) MaintenanceRecordType.REMINDER else currentState.recordType
            )
        }
    }

    private fun saveRecord() {
        _state.value.let { s ->
            // ... (Επικυρώσεις) ...

            val recordToSave = MaintenanceRecord(
                id = if (s.recordId == "new") UUID.randomUUID().toString() else s.recordId,
                vehicleId = s.vehicleId,
                recordType = s.recordType,
                title = s.description.take(50),
                description = s.description,
                date = s.date,
                odometer = s.odometer.toInt(),
                cost = if(s.recordType != MaintenanceRecordType.REMINDER) s.cost.toDoubleOrNull() else null,
                quantity = if(s.recordType == MaintenanceRecordType.FUEL_UP) s.quantity.toDoubleOrNull() else null,
                pricePerUnit = if(s.recordType == MaintenanceRecordType.FUEL_UP) s.pricePerUnit.toDoubleOrNull() else null,
                isReminder = s.isReminder,
                reminderDate = if (s.isReminder) s.reminderDate else null,
                reminderOdometer = if (s.isReminder) s.reminderOdometer.toIntOrNull() else null,
                isCompleted = false
            )

            viewModelScope.launch {
                try {
                    maintenanceRecordRepository.saveRecord(recordToSave)
                    _state.update { it.copy(error = null) }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Αποτυχία αποθήκευσης: ${e.message}") }
                }
            }
        }
    }

    // ... (Extensions) ...
}
// ... (defaultSuggestions) ...