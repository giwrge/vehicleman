package com.vehicleman.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vehicleman.presentation.vehicles.toVehicle
import com.vehicleman.presentation.vehicles.toFormState


/**
 * ViewModel για τη φόρμα εισαγωγής/επεξεργασίας οχήματος.
 * - Συνδέει το UI (VehicleFormScreen) με το Repository.
 * - Υποστηρίζει δημιουργία, ενημέρωση, διαγραφή και φόρτωση οχημάτων.
 */
@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    /** ----------------------------- **/
    /**         Ενέργειες Φόρμας     **/
    /** ----------------------------- **/

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.FieldChanged -> {
                _state.update { it.copyField(event.field, event.value) }
            }

            is VehicleFormEvent.LoadVehicle -> {
                loadVehicle(event.vehicleId)
            }

            is VehicleFormEvent.SaveVehicle -> {
                saveVehicle()
            }

            is VehicleFormEvent.DeleteVehicle -> {
                deleteVehicle(event.vehicleId)
            }
        }
    }

    /** ----------------------------- **/
    /** Φόρτωση οχήματος για Edit Mode **/
    /** ----------------------------- **/
    private fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    _state.update {
                        vehicle.toFormState().copy(isLoading = false)
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης")
                }
            }
        }
    }

    /** ----------------------------- **/
    /**  Αποθήκευση / Ενημέρωση      **/
    /** ----------------------------- **/
    private fun saveVehicle() {
        viewModelScope.launch {
            val form = _state.value
            val vehicle = form.toVehicle()

            _state.update { it.copy(isLoading = true, success = false, errorMessage = null) }

            try {
                val vehicleCount = vehicleRepository.getVehicleCount()

                // Αν είναι free έκδοση — μέχρι 3 οχήματα
                if (form.currentVehicle == null && vehicleCount >= 3) {
                    _state.update { it.copy(isLoading = false, limitReached = true) }
                    return@launch
                }

                // Αν υπάρχει ήδη το όχημα → update, αλλιώς insert
                if (form.currentVehicle != null) {
                    vehicleRepository.updateVehicle(vehicle)
                } else {
                    vehicleRepository.insertVehicle(vehicle)
                }

                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    /** ----------------------------- **/
    /**         Διαγραφή Οχήματος    **/
    /** ----------------------------- **/
    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, success = false, errorMessage = null) }
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    vehicleRepository.deleteVehicle(vehicle)
                    _state.update { it.copy(isLoading = false, success = true) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}
