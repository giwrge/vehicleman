package com.vehicleman.presentation.addeditvehicle

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
class AddEditVehiclePanelViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVehiclePanelState())
    val state: StateFlow<AddEditVehiclePanelState> = _state

    fun onEvent(event: AddEditVehiclePanelEvent) {
        when (event) {
            is AddEditVehiclePanelEvent.DeleteVehicleById -> {
                // This is now handled via ConfirmDelete
                _state.update { it.copy(vehicleToDelete = it.vehicles.find { v -> v.id == event.vehicleId }) }
            }
            is AddEditVehiclePanelEvent.ConfirmDelete -> {
                _state.value.vehicleToDelete?.let { vehicle ->
                    deleteVehicle(vehicle.id)
                }
            }
            is AddEditVehiclePanelEvent.CancelDelete -> {
                _state.update { it.copy(vehicleToDelete = null) }
            }
            is AddEditVehiclePanelEvent.RefreshVehicleList -> loadVehicles()
            is AddEditVehiclePanelEvent.ToggleAirflowCard -> toggleAirflowCard(event.vehicleId)
        }
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.getAllVehicles().collect { list ->
                    val uiItems = list.map { vehicle ->
                        VehicleDisplayItem(
                            id = vehicle.id,
                            name = vehicle.name,
                            makeModel = "${vehicle.make} ${vehicle.model}",
                            licensePlate = vehicle.plateNumber,
                            odometerText = "${vehicle.currentOdometer} km",
                            fuelTypes = vehicle.fuelTypes // Corrected: Pass the list directly
                        )
                    }
                    _state.update { it.copy(vehicles = uiItems, isLoading = false, vehicleToDelete = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage ?: "Σφάλμα φόρτωσης") }
            }
        }
    }

    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                repository.deleteVehicleById(vehicleId)
                loadVehicles() // Refresh the list after deletion
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage ?: "Αποτυχία διαγραφής") }
            }
        }
    }

    private fun toggleAirflowCard(vehicleId: String) {
        _state.update {
            val activeId = if (it.activeVehicleId == vehicleId) null else vehicleId
            it.copy(activeVehicleId = activeId)
        }
    }
}
