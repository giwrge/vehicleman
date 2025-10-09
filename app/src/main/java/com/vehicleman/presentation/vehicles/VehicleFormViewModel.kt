// app/src/main/java/com/vehicleman/presentation/vehicles/VehicleFormViewModel.kt
package com.vehicleman.presentation.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val repository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = checkNotNull(savedStateHandle["vehicleId"])

    private val _state = MutableStateFlow(VehicleFormState(vehicleId = vehicleId))
    val state: StateFlow<VehicleFormState> = _state

    init {
        loadVehicle()
    }

    private fun loadVehicle() {
        viewModelScope.launch {
            if (!vehicleId.isNewVehicle()) {
                val vehicle = repository.getVehicleById(vehicleId)
                vehicle?.let {
                    _state.update { currentState ->
                        currentState.copy(
                            name = it.name,
                            make = it.make,
                            model = it.model,
                            year = it.year.toString(),
                            licensePlate = it.licensePlate,
                            fuelType = it.fuelType,
                            initialOdometer = it.initialOdometer.toString(),
                            registrationDate = it.registrationDate,

                            // ΦΟΡΤΩΣΗ: Νέα πεδία
                            oilChangeIntervalKm = it.oilChangeIntervalKm.toString(),
                            oilChangeIntervalDays = it.oilChangeIntervalDays.toString()
                        )
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is VehicleFormEvent.OnMakeChange -> _state.update { it.copy(make = event.value) }
            is VehicleFormEvent.OnModelChange -> _state.update { it.copy(model = event.value) }
            is VehicleFormEvent.OnYearChange -> {
                if (event.value.isDigitsOnly() || event.value.isBlank()) {
                    _state.update { it.copy(year = event.value) }
                }
            }
            is VehicleFormEvent.OnLicensePlateChange -> _state.update { it.copy(licensePlate = event.value) }
            is VehicleFormEvent.OnFuelTypeChange -> _state.update { it.copy(fuelType = event.value) }
            is VehicleFormEvent.OnInitialOdometerChange -> {
                if (event.value.isDigitsOnly() || event.value.isBlank()) {
                    _state.update { it.copy(initialOdometer = event.value) }
                }
            }
            is VehicleFormEvent.OnRegistrationDateChange -> _state.update { it.copy(registrationDate = event.value) }

            // ΧΕΙΡΙΣΜΟΣ ΝΕΩΝ EVENTS
            is VehicleFormEvent.OnOilChangeKmChange -> {
                if (event.value.isDigitsOnly() || event.value.isBlank()) {
                    _state.update { it.copy(oilChangeIntervalKm = event.value) }
                }
            }
            is VehicleFormEvent.OnOilChangeDaysChange -> {
                if (event.value.isDigitsOnly() || event.value.isBlank()) {
                    _state.update { it.copy(oilChangeIntervalDays = event.value) }
                }
            }

            VehicleFormEvent.OnSaveVehicleClick -> saveVehicle()
            VehicleFormEvent.NavigationDone -> _state.update { it.copy(isSaved = false) }
        }
    }

    private fun saveVehicle() {
        // ... (Έλεγχοι επικύρωσης) ...

        if (state.value.name.isBlank() || state.value.make.isBlank() || state.value.model.isBlank()) {
            _state.update { it.copy(error = "Παρακαλώ συμπληρώστε όλα τα βασικά πεδία.") }
            return
        }

        val vehicle = Vehicle(
            id = if (vehicleId.isNewVehicle()) UUID.randomUUID().toString() else vehicleId,
            name = state.value.name.trim(),
            make = state.value.make.trim(),
            model = state.value.model.trim(),
            year = state.value.year.toIntOrNull() ?: 0,
            licensePlate = state.value.licensePlate.trim(),
            fuelType = state.value.fuelType.trim(),
            initialOdometer = state.value.initialOdometer.toIntOrNull() ?: 0,
            registrationDate = state.value.registrationDate,

            // ΑΠΟΘΗΚΕΥΣΗ: Νέα πεδία
            oilChangeIntervalKm = state.value.oilChangeIntervalKm.toIntOrNull() ?: 10000,
            oilChangeIntervalDays = state.value.oilChangeIntervalDays.toIntOrNull() ?: 365
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                repository.saveVehicle(vehicle)
                _state.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Αποτυχία αποθήκευσης: ${e.localizedMessage}", isLoading = false) }
            }
        }
    }

    private fun String.isNewVehicle(): Boolean = this.isBlank() || this == "new"

    private fun String.isDigitsOnly(): Boolean = this.matches(Regex("\\d*"))
}