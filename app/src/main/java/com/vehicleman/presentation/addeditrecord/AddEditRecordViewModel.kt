package com.vehicleman.presentation.addeditrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val vehicleRepository: VehicleRepository,
    private val categorizer: RecordCategorizerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditRecordState())
    val state = _state.asStateFlow()

    private val vehicleId: String =
        savedStateHandle.get<String>("vehicleId") ?: ""

    private val recordId: String? =
        savedStateHandle.get<String>("recordId")

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    init {
        if (vehicleId.isBlank()) {
            // Δεν μπορούμε να συνεχίσουμε χωρίς vehicleId
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Λείπει το όχημα για την εγγραφή."
                )
            }
        } else {
            _state.update { it.copy(vehicleId = vehicleId) }

            if (recordId == null || recordId == "new") {
                loadNewRecord()
            } else {
                loadExistingRecord(recordId)
            }

            loadVehicleFuelTypes()
        }
    }

    // -------------------------------------------------------------------------
    // LOAD VEHICLE FUEL TYPES (προς το παρόν κενό, μέχρι να μπουν τα fields στο Vehicle)
    // -------------------------------------------------------------------------

    private fun loadVehicleFuelTypes() {
        viewModelScope.launch {
            // Όταν προσθέσουμε στο Vehicle π.χ. primaryFuelType / supportedFuelTypes,
            // θα γεμίσουμε εδώ την λίστα.
            _state.update { s ->
                s.copy(
                    vehicleFuelTypes = emptyList(),
                    selectedFuelType = null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // NEW RECORD
    // -------------------------------------------------------------------------

    private fun loadNewRecord() {
        val now = Date()
        _state.update {
            it.copy(
                isLoading = false,
                isNew = true,
                date = now,
                dateText = dateFormat.format(now),
                category = null,
                suggestions = emptyList()
            )
        }
    }

    // -------------------------------------------------------------------------
    // LOAD EXISTING RECORD
    // -------------------------------------------------------------------------

    private fun loadExistingRecord(id: String) {
        viewModelScope.launch {
            val record = recordRepository.getRecordById(id)

            if (record == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Δεν βρέθηκε η εγγραφή."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    isNew = false,
                    recordId = record.id,
                    recordType = record.recordType,
                    title = record.title,
                    description = record.description ?: "",
                    date = record.date,
                    dateText = dateFormat.format(record.date),
                    odometer = record.odometer?.toString() ?: "",
                    cost = record.cost?.toString() ?: "",
                    quantity = record.quantity?.toString() ?: "",
                    pricePerUnit = record.pricePerUnit?.toString() ?: "",
                    fuelType = record.fuelType,
                    fuelTypeText = record.fuelType ?: "",
                    isReminder = record.isReminder,
                    isCompleted = record.isCompleted,
                    reminderDate = record.reminderDate,
                    reminderDateText = record.reminderDate?.let { d -> dateFormat.format(d) } ?: "",
                    reminderOdometer = record.reminderOdometer?.toString() ?: "",
                    isReminderSwitchLocked = record.isReminder,
                    showReminderFields = record.isReminder,
                    // την κατηγορία την ξανα-υπολογίζουμε από τίτλο/περιγραφή
                    category = null,
                    suggestions = emptyList()
                )
            }

            // Υπολογίζουμε εκ νέου την κατηγορία για την υπάρχουσα εγγραφή
            updateCategorization()
        }
    }

    // -------------------------------------------------------------------------
    // EVENT HANDLING
    // -------------------------------------------------------------------------

    fun onEvent(event: AddEditRecordEvent) {
        when (event) {

            is AddEditRecordEvent.LoadExisting -> {
                // Αν τυχόν το καλέσεις από UI
                loadExistingRecord(event.recordId)
            }

            is AddEditRecordEvent.TitleChanged -> {
                _state.update { it.copy(title = event.value) }
                updateCategorization()
                updateSuggestions()
                validateSave()
            }

            is AddEditRecordEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.value) }
                updateCategorization()
                validateSave()
            }

            is AddEditRecordEvent.DateTextChanged -> {
                _state.update { it.copy(dateText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.DateChanged -> {
                _state.update {
                    it.copy(
                        date = event.value,
                        dateText = dateFormat.format(event.value)
                    )
                }
                validateSave()
            }

            is AddEditRecordEvent.OdometerChanged -> {
                _state.update { it.copy(odometer = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.CostChanged -> {
                _state.update { it.copy(cost = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.QuantityChanged -> {
                _state.update { it.copy(quantity = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.PricePerUnitChanged -> {
                _state.update { it.copy(pricePerUnit = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.FuelTypeChanged -> {
                _state.update { it.copy(fuelTypeText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.RecordTypeChanged -> {
                handleRecordTypeChange(event.value)
            }

            is AddEditRecordEvent.SuggestionClicked -> {
                _state.update { it.copy(title = event.suggestion) }
                updateCategorization()
                validateSave()
            }

            is AddEditRecordEvent.ToggleReminder -> {
                if (!_state.value.isReminderSwitchLocked) {
                    _state.update {
                        it.copy(
                            isReminder = event.value,
                            showReminderFields = event.value
                        )
                    }
                }
                validateSave()
            }

            is AddEditRecordEvent.ReminderDateTextChanged -> {
                _state.update { it.copy(reminderDateText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.ReminderDateChanged -> {
                _state.update {
                    it.copy(
                        reminderDate = event.value,
                        reminderDateText = dateFormat.format(event.value)
                    )
                }
                validateSave()
            }

            is AddEditRecordEvent.ReminderOdometerChanged -> {
                _state.update { it.copy(reminderOdometer = event.value) }
                validateSave()
            }

            AddEditRecordEvent.ToggleCompleted -> {
                _state.update { it.copy(isCompleted = !it.isCompleted) }
                validateSave()
            }

            AddEditRecordEvent.Save -> saveRecord()

            AddEditRecordEvent.ErrorShown -> {
                _state.update { it.copy(errorMessage = null) }
            }

            AddEditRecordEvent.NavigateBackConsumed -> {
                _state.update { it.copy(navigateBack = false) }
            }
        }
    }

    // -------------------------------------------------------------------------
    // HANDLE RECORD TYPE CHANGE
    // -------------------------------------------------------------------------

    private fun handleRecordTypeChange(type: RecordType) {
        _state.update {
            it.copy(
                recordType = type,
                showCostDetails = type == RecordType.FUEL_UP,
                showFuelTypeSelection = type == RecordType.FUEL_UP,
                isReminder = type == RecordType.REMINDER,
                showReminderFields = type == RecordType.REMINDER,
                isReminderSwitchLocked = type == RecordType.REMINDER
            )
        }
        updateCategorization()
        validateSave()
    }

    // -------------------------------------------------------------------------
    // CATEGORIZER
    // -------------------------------------------------------------------------

    private fun updateCategorization() {
        val s = _state.value

        if (s.title.isBlank() && s.description.isBlank()) {
            _state.update { it.copy(category = null) }
            return
        }

        val category: RecordCategory = categorizer(
            title = s.title,
            isReminder = s.recordType == RecordType.REMINDER,
            description = s.description
        )

        _state.update { it.copy(category = category) }
    }

    // -------------------------------------------------------------------------
    // SUGGESTIONS (AUTOCOMPLETE)
    // -------------------------------------------------------------------------

    private fun updateSuggestions() {
        val input = _state.value.title.lowercase()

        val base = listOf(
            "λάδια", "service", "φρένα", "μπουζί",
            "air filter", "oil filter",
            "gas", "fuel", "unleaded", "diesel",
            "τροχοί", "λάστιχα", "αντισκωριακό"
        )

        val suggestions = base.filter {
            input.isNotBlank() && it.contains(input)
        }

        _state.update { it.copy(suggestions = suggestions) }
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    private fun validateSave() {
        val s = _state.value
        val valid =
            s.title.isNotBlank() &&
                    s.dateText.isNotBlank()

        _state.update { it.copy(isSaveEnabled = valid) }
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    private fun saveRecord() {
        viewModelScope.launch {
            try {
                val s = _state.value

                val reminderOdoInt: Int? =
                    s.reminderOdometer.toIntOrNull()

                val record = Record(
                    id = s.recordId ?: java.util.UUID.randomUUID().toString(),
                    vehicleId = s.vehicleId,
                    recordType = s.recordType,
                    title = s.title,
                    description = s.description.ifBlank { null },
                    date = s.date,
                    odometer = s.odometer.toIntOrNull() ?: 0,
                    cost = s.cost.toDoubleOrNull(),
                    quantity = s.quantity.toDoubleOrNull(),
                    pricePerUnit = s.pricePerUnit.toDoubleOrNull(),
                    fuelType = s.fuelTypeText.ifBlank { null },
                    isReminder = s.isReminder,
                    reminderDate = s.reminderDate,
                    // Record.reminderOdometer είναι Int, άρα:
                    reminderOdometer = reminderOdoInt ?: 0,
                    isCompleted = s.isCompleted
                )


                recordRepository.saveRecord(record)

                _state.update { it.copy(navigateBack = true) }

            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = "Σφάλμα κατά την αποθήκευση.")
                }
            }
        }
    }
}
