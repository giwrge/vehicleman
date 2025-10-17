package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel UI επιπέδου για Add/Edit Vehicle.
 * - Συνδέει το presentation layer (VehicleFormViewModel) με το UI (Compose)
 * - Χειρίζεται αποθήκευση, φόρτωση και ενημέρωση.
 */
@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    /** Επεξεργασία αλλαγών φόρμας **/
    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.FieldChanged -> {
                _state.update { it.copyField(event.field, event.value) }
            }

            is VehicleFormEvent.LoadVehicle -> loadVehicle(event.vehicleId)
            is VehicleFormEvent.SaveVehicle -> saveVehicle(event.vehicle)
            is VehicleFormEvent.DeleteVehicle -> deleteVehicle(event.vehicleId)
        }
    }

    /** Φόρτωση υπάρχοντος οχήματος **/
    private fun loadVehicle(id: String) {
        viewModelScope.launch {
            vehicleRepository.getVehicleById(id)?.let { vehicle ->
                _state.update {
                    it.copy(
                        brand = vehicle.brand,
                        model = vehicle.model,
                        plate = vehicle.plate,
                        year = vehicle.year?.toString() ?: "",
                        odometer = vehicle.odometer?.toString() ?: "",
                        oilChangeTime = vehicle.oilChangeTime ?: "",
                        oilChangeKm = vehicle.oilChangeKm?.toString() ?: "",
                        tiresChangeTime = vehicle.tiresChangeTime ?: "",
                        tiresChangeKm = vehicle.tiresChangeKm?.toString() ?: "",
                        insuranceDate = vehicle.insurancePaymentDate ?: "",
                        taxDate = vehicle.taxesPaymentDate ?: "",
                        currentVehicle = vehicle
                    )
                }
            }
        }
    }

    /** Αποθήκευση ή ενημέρωση **/
    private fun saveVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                if (vehicle.id == null) vehicleRepository.insertVehicle(vehicle)
                else vehicleRepository.updateVehicle(vehicle)

                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    /** Διαγραφή **/
    private fun deleteVehicle(id: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicle(id)
                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }
}
