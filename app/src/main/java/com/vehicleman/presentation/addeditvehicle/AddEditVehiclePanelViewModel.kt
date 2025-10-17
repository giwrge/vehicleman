package com.vehicleman.presentation.addeditvehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle   // ✅ ΜΟΝΟ αυτό
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.presentation.vehicles.toVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για Add/Edit Vehicle οθόνη (AddEditVehicleScreen).
 * Επικοινωνεί με το Repository και κρατάει την κατάσταση της φόρμας.
 */
@HiltViewModel
class AddEditVehiclePanelViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.FieldChanged -> {
                _state.update { it.copyField(event.field, event.value) }
            }

            is VehicleFormEvent.LoadVehicle -> loadVehicle(event.vehicleId)

            is VehicleFormEvent.SaveVehicle -> saveVehicle(_state.value.toVehicle())

            is VehicleFormEvent.DeleteVehicle -> deleteVehicle(event.vehicleId)
        }
    }

    /** Φόρτωση υπάρχοντος οχήματος από repository **/
    private fun loadVehicle(id: String) {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(id)
            vehicle?.let {
                _state.update {
                    it.copy(
                        brand = it.make,                // ← το make από το domain model
                        model = it.model,
                        plate = it.licensePlate,
                        year = it.year.toString(),
                        odometer = it.initialOdometer.toString(),
                        oilChangeKm = it.oilChangeIntervalKm.toString(),
                        oilChangeTime = it.oilChangeIntervalDays.toString(),
                        fuelType = it.fuelType,
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
                if (vehicleRepository.getVehicleById(vehicle.id) == null)
                    vehicleRepository.insertVehicle(vehicle)
                else
                    vehicleRepository.updateVehicle(vehicle)

                _state.update { it.copy(success = true, errorMessage = null) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία αποθήκευσης") }
            }
        }
    }

    /** Διαγραφή **/
    private fun deleteVehicle(id: String) {
        viewModelScope.launch {
            try {
                val vehicle = vehicleRepository.getVehicleById(id)
                vehicle?.let {
                    vehicleRepository.deleteVehicle(it)
                    _state.update { it.copy(success = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία διαγραφής") }
            }
        }
    }
}
