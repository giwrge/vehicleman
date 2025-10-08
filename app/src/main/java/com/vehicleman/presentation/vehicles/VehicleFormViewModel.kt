package com.vehicleman.presentation.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.ui.navigation.NavDestinations // Υποθέτουμε ότι το NavDestinations είναι εδώ
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel για την οθόνη προσθήκης/επεξεργασίας οχήματος.
 */
@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val repository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    private var currentVehicleId: String? = null

    init {
        // Ελέγχει αν πρόκειται για Edit Mode
        savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)?.let { vehicleId ->
            if (vehicleId != "new") {
                currentVehicleId = vehicleId
                loadVehicle(vehicleId)
            } else {
                _state.update { it.copy(isReady = true) }
            }
        } ?: run {
            _state.update { it.copy(isReady = true) }
        }
    }

    private fun loadVehicle(vehicleId: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getVehicleById(vehicleId)?.let { vehicle ->
                _state.update {
                    it.copy(
                        name = vehicle.name,
                        make = vehicle.make,
                        model = vehicle.model,
                        year = vehicle.year.toString(),
                        licensePlate = vehicle.licensePlate,
                        fuelType = vehicle.fuelType,
                        initialOdometer = vehicle.initialOdometer.toString(),
                        isEditMode = true,
                        isLoading = false,
                        isReady = true
                    )
                }
            } ?: _state.update {
                it.copy(
                    isLoading = false,
                    isReady = true,
                    validationErrors = it.validationErrors.copy(generalError = "Το όχημα δεν βρέθηκε.")
                )
            }
        }
    }

    /** Χειρίζεται τα Events από το UI. */
    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.NameChanged -> _state.update { it.copy(name = event.name, validationErrors = it.validationErrors.copy(nameError = null, generalError = null)) }
            is VehicleFormEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is VehicleFormEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is VehicleFormEvent.YearChanged -> _state.update { it.copy(year = event.year.filter { char -> char.isDigit() }.take(4)) }
            is VehicleFormEvent.LicensePlateChanged -> _state.update { it.copy(licensePlate = event.licensePlate.uppercase().take(8)) }
            is VehicleFormEvent.FuelTypeSelected -> _state.update { it.copy(fuelType = event.fuelType) }
            is VehicleFormEvent.InitialOdometerChanged -> _state.update { it.copy(initialOdometer = event.odometer.filter { char -> char.isDigit() }, validationErrors = it.validationErrors.copy(initialOdometerError = null, generalError = null)) }

            VehicleFormEvent.SaveVehicle -> saveVehicle()
        }
    }

    private fun saveVehicle() {
        val current = _state.value
        // Επικύρωση
        val isNameValid = current.name.isNotBlank()
        val odometerInt = current.initialOdometer.toIntOrNull()
        val isOdometerValid = odometerInt != null && odometerInt >= 0

        if (!isNameValid || !isOdometerValid) {
            _state.update {
                it.copy(
                    validationErrors = it.validationErrors.copy(
                        nameError = if (!isNameValid) "Το Όνομα είναι υποχρεωτικό." else null,
                        initialOdometerError = if (!isOdometerValid) "Τα Χιλιόμετρα πρέπει να είναι αριθμός >= 0." else null,
                        generalError = "Παρακαλώ συμπληρώστε τα υποχρεωτικά πεδία."
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Έλεγχος Ορίου (μόνο για νέα οχήματα)
            if (currentVehicleId == null) {
                val currentCount = repository.getVehicleCount()
                val maxFreeVehicles = 4
                if (currentCount >= maxFreeVehicles) {
                    _state.update {
                        it.copy(
                            showPaywall = true,
                            isLoading = false,
                            validationErrors = it.validationErrors.copy(generalError = "Έχετε φτάσει το όριο των $maxFreeVehicles οχημάτων (PRO Feature).")
                        )
                    }
                    return@launch
                }
            }

            // Δημιουργία/Ενημέρωση Domain Model
            val vehicleToSave = Vehicle(
                id = currentVehicleId ?: UUID.randomUUID().toString(),
                name = current.name,
                make = current.make,
                model = current.model,
                year = current.year.toIntOrNull() ?: 0,
                licensePlate = current.licensePlate,
                fuelType = current.fuelType,
                initialOdometer = odometerInt ?: 0,
                registrationDate = System.currentTimeMillis()
            )

            try {
                if (currentVehicleId != null) {
                    repository.updateVehicle(vehicleToSave)
                } else {
                    repository.insertVehicle(vehicleToSave)
                }
                _state.update { it.copy(isSavedSuccess = true, isLoading = false, validationErrors = VehicleFormErrorState()) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        validationErrors = it.validationErrors.copy(generalError = "Αποτυχία αποθήκευσης: ${e.message}")
                    )
                }
            }
        }
    }
}