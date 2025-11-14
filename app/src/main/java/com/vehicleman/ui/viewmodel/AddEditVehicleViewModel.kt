package com.vehicleman.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.UserStatus
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.presentation.vehicles.toFormState
import com.vehicleman.presentation.vehicles.toVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is VehicleFormEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is VehicleFormEvent.PlateNumberChanged -> _state.update { it.copy(plateNumber = event.plateNumber) }
            is VehicleFormEvent.YearChanged -> _state.update { it.copy(year = event.year) }
            is VehicleFormEvent.FuelTypeChanged -> _state.update { it.copy(fuelTypes = event.fuelTypes) } // Corrected
            is VehicleFormEvent.CurrentOdometerChanged -> _state.update { it.copy(currentOdometer = event.currentOdometer) }
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

    fun resetState() {
        _state.value = VehicleFormState()
    }

    private fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            if (vehicleId == "new") {
                _state.value = VehicleFormState()
                return@launch
            }
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    _state.value = vehicle.toFormState()
                } else {
                    _state.update { it.copy(errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης οχήματος") }
            }
        }
    }

    private fun saveVehicle() {
        viewModelScope.launch {

            val formState = _state.value
            if (formState.make.isBlank() || formState.model.isBlank()) {
                _state.update { it.copy(errorMessage = "Η Μάρκα και το Μοντέλο είναι υποχρεωτικά") }
                return@launch
            }

            val user = userPreferencesRepository.user.first()
            val vehicleCount = vehicleRepository.getVehicleCount()

            if ((_state.value.id == null || _state.value.id == "new") && !user.isTestMode) { // Only check for new vehicles and not in test mode
                val limit = when {
                    user.proLevel == ProLevel.PRO_2 -> 10
                    user.proLevel == ProLevel.PRO_1 -> 7
                    user.status == UserStatus.SIGNED_UP -> 3
                    else -> 3 // Free user
                }
                if (vehicleCount >= limit) {
                    _state.update { it.copy(shouldNavigateToProMode = true) }
                    return@launch
                }
            }
            
            val vehicle = formState.toVehicle()
            val finalVehicle = if (vehicle.id.isBlank() || vehicle.id == "new") {
                vehicle.copy(
                    id = UUID.randomUUID().toString(), // Generate new ID
                    dateAdded = System.currentTimeMillis(), 
                    lastModified = System.currentTimeMillis()
                )
            } else {
                vehicle.copy(lastModified = System.currentTimeMillis())
            }

            try {
                vehicleRepository.saveVehicle(finalVehicle)
                _state.update { it.copy(isFormValid = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία αποθήκευσης οχήματος") }
            }
        }
    }

    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicleById(vehicleId)
                _state.update { it.copy(isFormValid = true) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία διαγραφής") }
            }
        }
    }
}