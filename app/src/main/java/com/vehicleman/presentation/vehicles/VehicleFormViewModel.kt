package com.vehicleman.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is VehicleFormEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is VehicleFormEvent.PlateNumberChanged -> _state.update { it.copy(plateNumber = event.plateNumber) }
            is VehicleFormEvent.YearChanged -> _state.update { it.copy(year = event.year) }
            is VehicleFormEvent.CurrentOdometerChanged -> _state.update { it.copy(currentOdometer = event.currentOdometer) }
            is VehicleFormEvent.FuelTypeChanged -> _state.update { it.copy(fuelTypes = event.fuelTypes) }
            is VehicleFormEvent.OilChangeKmChanged -> _state.update { it.copy(oilChangeKm = event.oilChangeKm) }
            is VehicleFormEvent.OilChangeDateChanged -> _state.update { it.copy(oilChangeDate = event.oilChangeDate) }
            is VehicleFormEvent.TiresChangeKmChanged -> _state.update { it.copy(tiresChangeKm = event.tiresChangeKm) }
            is VehicleFormEvent.TiresChangeDateChanged -> _state.update { it.copy(tiresChangeDate = event.tiresChangeDate) }
            is VehicleFormEvent.InsuranceExpiryDateChanged -> _state.update { it.copy(insuranceExpiryDate = event.insuranceExpiryDate) }
            is VehicleFormEvent.TaxesExpiryDateChanged -> _state.update { it.copy(taxesExpiryDate = event.taxesExpiryDate) }
            is VehicleFormEvent.LoadVehicle -> loadVehicle(event.vehicleId)
            is VehicleFormEvent.DeleteVehicle -> deleteVehicle(event.vehicleId)
            is VehicleFormEvent.Submit -> saveVehicle()
        }
    }

    private fun loadVehicle(vehicleId: String) {
        if (vehicleId == "new") {
            _state.update { VehicleFormState() } // Reset for new vehicle
            return
        }
        viewModelScope.launch {
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    _state.update { vehicle.toFormState() }
                } else {
                    _state.update { it.copy(errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης")
                }
            }
        }
    }

    private fun saveVehicle() {
        viewModelScope.launch {
            val formState = _state.value
            if (formState.make.isBlank() || formState.model.isBlank() || formState.plateNumber.isBlank()) {
                _state.update { it.copy(errorMessage = "Τα πεδία Μάρκα, Μοντέλο και Πινακίδα είναι υποχρεωτικά.") }
                return@launch
            }

            val vehicle = formState.toVehicle()

            try {
                vehicleRepository.saveVehicle(vehicle)
                _state.update { it.copy(isFormValid = true, errorMessage = null) } // Navigate back on success
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicleById(vehicleId)
                _state.update { it.copy(isFormValid = true, errorMessage = null) } // Navigate back on success
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }
}
