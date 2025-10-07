package com.vehicleman.presentation.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel για την οθόνη προσθήκης/επεξεργασίας οχήματος.
 * Χρησιμοποιεί το SavedStateHandle για να διαβάσει το vehicleId από την πλοήγηση.
 */
@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val repository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Χρησιμοποιούμε την κατάσταση που όρισες
    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    // Το vehicleId διαχειρίζεται πλέον εντός της VehicleFormState
    private var isVehicleLoaded = false // Χρησιμοποιείται για να μην καλείται η φόρτωση πολλές φορές

    init {
        // Ελέγχει αν πρόκειται για Edit Mode διαβάζοντας το 'vehicleId' από τα arguments
        savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)?.let { vehicleId ->
            if (vehicleId != "new") {
                _state.update { it.copy(vehicleId = vehicleId, isEditMode = true, isLoading = true) }
                loadVehicle(vehicleId)
            } else {
                // Αν πρόκειται για νέα καταχώρηση, η φόρμα είναι έτοιμη
                _state.update { it.copy(isLoading = false) }
            }
        } ?: run {
            // Σε περίπτωση που λείπει το ID, θεωρούμε ότι είναι νέα καταχώρηση
            _state.update { it.copy(isLoading = false) }
        }
    }

    /** Φορτώνει τα δεδομένα του οχήματος για επεξεργασία. */
    private fun loadVehicle(vehicleId: String) {
        if (isVehicleLoaded) return // Αποτροπή διπλής φόρτωσης
        isVehicleLoaded = true

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
                        isLoading = false,
                        isEditMode = true,
                    )
                }
            } ?: run {
                // Χειρισμός σφάλματος αν το όχημα δεν βρεθεί
                _state.update {
                    it.copy(
                        isLoading = false,
                        validationErrors = it.validationErrors.copy(generalError = "Το όχημα δεν βρέθηκε.")
                    )
                }
            }
        }
    }

    /** Χειρίζεται τα Events από το UI. */
    fun onEvent(event: VehicleFormEvent) { // Χρησιμοποιούμε το VehicleFormEvent
        when (event) {
            is VehicleFormEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is VehicleFormEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is VehicleFormEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is VehicleFormEvent.YearChanged -> _state.update { it.copy(year = event.year.filter { char -> char.isDigit() }.take(4)) }
            is VehicleFormEvent.LicensePlateChanged -> _state.update { it.copy(licensePlate = event.licensePlate.uppercase().take(8)) }
            is VehicleFormEvent.FuelTypeChanged -> _state.update { it.copy(fuelType = event.fuelType) } // Διόρθωση ονόματος event
            is VehicleFormEvent.InitialOdometerChanged -> _state.update { it.copy(initialOdometer = event.odometer.filter { char -> char.isDigit() }) }
            VehicleFormEvent.SaveVehicle -> saveVehicle()
            VehicleFormEvent.DismissGeneralError -> _state.update { it.copy(validationErrors = it.validationErrors.copy(generalError = null)) }
        }
    }

    /** Υλοποιεί τη λογική αποθήκευσης (Insert/Update) και τον έλεγχο ορίου. */
    private fun saveVehicle() {
        val current = _state.value

        // Επικύρωση
        if (current.name.isBlank() || current.initialOdometer.toIntOrNull() == null || (current.initialOdometer.toIntOrNull() ?: 0) <= 0) {
            _state.update {
                it.copy(
                    validationErrors = it.validationErrors.copy(
                        generalError = "Το Όνομα και τα Αρχικά Χιλιόμετρα είναι υποχρεωτικά και πρέπει να είναι > 0."
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            // Ο ΕΛΕΓΧΟΣ ΟΡΙΟΥ ΑΠΑΙΤΕΙ ΤΟ getVehicleCount()
            if (!current.isEditMode) {
                val currentCount = repository.getVehicleCount()
                val maxFreeVehicles = 4
                if (currentCount >= maxFreeVehicles) {
                    _state.update {
                        it.copy(
                            showPaywall = true,
                            isSaved = false,
                            validationErrors = it.validationErrors.copy(generalError = "Έχετε φτάσει το όριο των $maxFreeVehicles οχημάτων (PRO Feature).")
                        )
                    }
                    return@launch
                }
            }

            // Δημιουργία/Ενημέρωση Domain Model
            val vehicleToSave = Vehicle(
                id = current.vehicleId ?: UUID.randomUUID().toString(),
                name = current.name,
                make = current.make,
                model = current.model,
                year = current.year.toIntOrNull() ?: 0,
                licensePlate = current.licensePlate,
                fuelType = current.fuelType,
                initialOdometer = current.initialOdometer.toInt(),
                registrationDate = System.currentTimeMillis() // Το registrationDate θα ενημερωθεί αν υπάρχει ήδη.
            )

            try {
                if (current.isEditMode) {
                    repository.updateVehicle(vehicleToSave)
                } else {
                    repository.insertVehicle(vehicleToSave)
                }
                _state.update { it.copy(isSaved = true, validationErrors = VehicleFormErrorState()) } // Καθαρίζουμε τα σφάλματα επιτυχώς
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaved = false,
                        validationErrors = it.validationErrors.copy(generalError = "Αποτυχία αποθήκευσης: ${e.message}")
                    )
                }
            }
        }
    }
}