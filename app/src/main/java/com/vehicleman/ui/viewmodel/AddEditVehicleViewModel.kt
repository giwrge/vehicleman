package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.presentation.vehicles.toVehicle
import com.vehicleman.presentation.vehicles.toFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για τη φόρμα Προσθήκης/Επεξεργασίας Οχήματος.
 */
@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.FieldChanged -> {
                _state.update { it.copyField(event.fieldName, event.value) }
            }

            is VehicleFormEvent.LoadVehicle -> {
                loadVehicle(event.vehicleId)
            }

            is VehicleFormEvent.SaveVehicle -> {
                saveVehicle()
            }

            is VehicleFormEvent.DeleteVehicle -> deleteVehicle(event.vehicleId)
            else -> {}
        }
    }

    /** Φόρτωση υπάρχοντος οχήματος (edit mode) **/
    private fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            //_state.update { it.copy(isLoading = true) } // isLoading is not on the state
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    _state.update { vehicle.toFormState()/* .copy(isLoading = false) */ }
                } else {
                    _state.update { it.copy(errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης οχήματος") }
            }
        }
    }

    /** Αποθήκευση (Εισαγωγή ή Ενημέρωση) Οχήματος **/
    private fun saveVehicle() {
        viewModelScope.launch {
            val vehicle = state.value.toVehicle()
            //_state.update { it.copy(isLoading = true) }
            try {
                val existing = vehicleRepository.getVehicleById(vehicle.id)
                if (existing == null) {
                    vehicleRepository.insertVehicle(vehicle)
                } else {
                    vehicleRepository.updateVehicle(vehicle)
                }
                //_state.update { it.copy(isLoading = false, success = true) } // success is not on the state
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία αποθήκευσης οχήματος") }
            }
        }
    }

    /** Διαγραφή Οχήματος **/
    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicleById(vehicleId)
                //_state.update { it.copy(success = true) } // success is not on the state
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία διαγραφής") }
            }
        }
    }
}
