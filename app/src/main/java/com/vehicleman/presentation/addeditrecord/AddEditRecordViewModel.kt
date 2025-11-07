package com.vehicleman.presentation.addeditrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.UserStatus
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

private val defaultSuggestions = listOf("Service", "Oil Change", "Tires", "Fuel", "KTEO", "Insurance")

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val vehicleRepository: VehicleRepository, // Injected VehicleRepository
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String? = savedStateHandle[NavDestinations.VEHICLE_ID_KEY]
    private val recordId: String? = savedStateHandle["recordId"]

    private val _state = MutableStateFlow(AddEditRecordState())
    val state: StateFlow<AddEditRecordState> = _state

    init {
        if (vehicleId == null) {
            _state.update { it.copy(error = "Vehicle ID is missing.") }
        } else {
            _state.update { it.copy(vehicleId = vehicleId) }
            loadVehicleFuelTypes(vehicleId)
            loadLatestOdometer(vehicleId)
            if (recordId != null) {
                _state.update { it.copy(recordId = recordId) }
                loadRecord(recordId)
            }
        }
    }

    private fun loadVehicleFuelTypes(vehicleId: String) {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            vehicle?.let {
                _state.update { state -> state.copy(vehicleFuelTypes = it.fuelTypes) }
            }
        }
    }

    private fun loadLatestOdometer(vehicleId: String) {
        viewModelScope.launch {
            val latestOdometer = recordRepository.getLatestOdometer(vehicleId)
            if (recordId == "new" && latestOdometer != null && latestOdometer > 0) {
                _state.update { it.copy(odometer = latestOdometer.toString()) }
            }
        }
    }

    private fun loadRecord(recordId: String) {
        if (recordId != "new") {
            viewModelScope.launch {
                try {
                    val record = recordRepository.getRecordById(recordId)
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
                                selectedFuelType = record.fuelType,
                                showFuelTypeSelection = record.recordType == RecordType.FUEL_UP && currentState.vehicleFuelTypes.size > 1,
                                showReminderFields = record.isReminder,
                                showCostDetails = record.recordType != RecordType.REMINDER
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
            is AddEditRecordEvent.OnFuelTypeSelected -> _state.update { it.copy(selectedFuelType = event.fuelType) }
            AddEditRecordEvent.OnSaveClicked -> saveRecord()
            AddEditRecordEvent.OnTwoTapActivated -> { /* Logic for 2-tap activation */ }
        }
    }

    private fun handleSuggestionChipClicked(suggestion: String) {
        val currentDesc = _state.value.description
        val newDesc = if (currentDesc.isEmpty()) suggestion else "$currentDesc $suggestion"
        handleSmartInput(newDesc)
    }

    private fun handlePricePerUnitChange(price: String) {
        _state.update { it.copy(pricePerUnit = price) }
        val quantity = _state.value.quantity.toDoubleOrNull()
        val pricePerUnit = price.toDoubleOrNull()
        if (quantity != null && pricePerUnit != null) {
            val cost = (quantity * pricePerUnit).roundTo(2)
            _state.update { it.copy(cost = cost.toString()) }
        }
    }

    private fun handleSmartInput(text: String) {
        val normalizedText = text.lowercase()
        val newRecordType = when {
            normalizedText.contains("βενζίνη") || normalizedText.contains("πετρέλαιο") || normalizedText.contains("υγραέριο") -> RecordType.FUEL_UP
            normalizedText.contains("υπενθύμιση") || normalizedText.contains("ασφάλεια") || normalizedText.contains("ΚΤΕΟ") -> RecordType.REMINDER
            else -> RecordType.EXPENSE
        }

        val numbers = "-?(\\d+(\\.\\d+)?)".toRegex().findAll(text).map { it.value.toDouble() }.toList()

        var cost = ""
        var quantity = ""
        var pricePerUnit = ""

        if (newRecordType == RecordType.FUEL_UP) {
            if (numbers.size == 2) {
                quantity = numbers[0].toString()
                pricePerUnit = numbers[1].toString()
                cost = (numbers[0] * numbers[1]).roundTo(2).toString()
            } else if (numbers.size == 1) {
                cost = numbers[0].toString()
            }
        } else if (newRecordType == RecordType.EXPENSE && numbers.isNotEmpty()) {
            cost = numbers.first().toString()
        }

        val showFuelSelection = newRecordType == RecordType.FUEL_UP && _state.value.vehicleFuelTypes.size > 1
        val selectedFuel = if (newRecordType == RecordType.FUEL_UP && _state.value.vehicleFuelTypes.size == 1) {
            _state.value.vehicleFuelTypes.first()
        } else if (newRecordType != RecordType.FUEL_UP) {
            null
        } else {
            _state.value.selectedFuelType
        }

        _state.update {
            it.copy(
                description = text,
                recordType = newRecordType,
                cost = cost,
                quantity = quantity,
                pricePerUnit = pricePerUnit,
                showCostDetails = newRecordType != RecordType.REMINDER,
                showFuelTypeSelection = showFuelSelection,
                selectedFuelType = selectedFuel
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
                recordType = if (isFuture) RecordType.REMINDER else RecordType.EXPENSE
            )
        }
    }

    private fun saveRecord() {
        viewModelScope.launch {
            val user = userPreferencesRepository.user.first()
            val recordCount = userPreferencesRepository.recordCreationCount.first()

            if (!user.isTestMode) { // Bypass limits in test mode
                if (user.status == UserStatus.FREE && recordCount >= 30) {
                    _state.update { it.copy(shouldNavigateToSignup = true) }
                    return@launch
                }

                if (user.status == UserStatus.SIGNED_UP && user.proLevel == ProLevel.NONE && recordCount >= 150) {
                    _state.update { it.copy(shouldNavigateToProMode = true) }
                    return@launch
                }
            }

            _state.value.let { s ->
                if (s.vehicleId.isBlank()) { // Check against the state's vehicleId
                    _state.update { it.copy(error = "Vehicle ID is missing.") }
                    return@let
                }
                if (s.odometer.isBlank()) {
                    _state.update { it.copy(error = "Τα χιλιόμετρα είναι υποχρεωτικά") }
                    return@let
                }

                val odometerValue = s.odometer.toIntOrNull()
                if (odometerValue == null) {
                    _state.update { it.copy(error = "Τα χιλιόμετρα πρέπει να είναι έγκυρος αριθμός") }
                    return@let
                }

                val recordToSave = Record(
                    id = if (s.recordId == "new") UUID.randomUUID().toString() else s.recordId,
                    vehicleId = s.vehicleId,
                    recordType = s.recordType,
                    title = s.description.take(50),
                    description = s.description,
                    date = s.date,
                    odometer = odometerValue,
                    cost = if (s.recordType != RecordType.REMINDER) s.cost.toDoubleOrNull() else null,
                    quantity = if (s.recordType == RecordType.FUEL_UP) s.quantity.toDoubleOrNull() else null,
                    pricePerUnit = if (s.recordType == RecordType.FUEL_UP) s.pricePerUnit.toDoubleOrNull() else null,
                    fuelType = if (s.recordType == RecordType.FUEL_UP) s.selectedFuelType else null,
                    isReminder = s.isReminder,
                    reminderDate = if (s.isReminder) s.reminderDate else null,
                    reminderOdometer = if (s.isReminder) s.reminderOdometer.toIntOrNull() else null,
                    isCompleted = false
                )

                try {
                    recordRepository.saveRecord(recordToSave)
                    if (s.recordId == "new") {
                        userPreferencesRepository.incrementRecordCreationCount()
                    }
                    _state.update { it.copy(error = null, isSaveSuccess = true) } // <-- ΣΗΜΑΝΤΙΚΗ ΑΛΛΑΓΗ: Ενημέρωσε το isSaveSuccess
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Αποτυχία αποθήκευσης: ${e.message}") }
                }
            }
        }
    }


    private fun Date.startOfDay(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun Double.roundTo(decimals: Int): Double {
        val multiplier = 10.0.pow(decimals)
        return (this * multiplier).roundToInt() / multiplier
    }
}
