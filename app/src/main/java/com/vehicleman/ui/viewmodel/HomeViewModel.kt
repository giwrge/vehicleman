package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelState
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVehiclePanelState(isLoading = true))
    val state: StateFlow<AddEditVehiclePanelState> = _state

    init {
        loadVehicles()
    }

    fun onEvent(event: AddEditVehiclePanelEvent) {
        when (event) {
            is AddEditVehiclePanelEvent.DeleteVehicleById -> deleteVehicle(event.vehicleId)
            is AddEditVehiclePanelEvent.RefreshVehicleList -> loadVehicles()
            is AddEditVehiclePanelEvent.ToggleAirflowCard -> toggleAirflowCard(event.vehicleId)
            else -> Unit
        }
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            try {
                vehicleRepository.getAllVehicles().collectLatest { vehicles ->
                    val uiList = vehicles.map { v ->
                        VehicleDisplayItem(
                            id = v.id,
                            name = "${v.make} ${v.model}".trim(),
                            makeModel = "${v.make} • ${v.model}",
                            licensePlate = v.licensePlate,
                            odometerText = "${v.initialOdometer} km",
                            isActive = false
                        )
                    }
                    _state.update { it.copy(vehicles = uiList, isLoading = false, error = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Σφάλμα") }
            }
        }
    }

    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicleById(vehicleId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage ?: "Αποτυχία διαγραφής") }
            }
        }
    }

    private fun toggleAirflowCard(vehicleId: String) {
        _state.update { current ->
            current.copy(
                vehicles = current.vehicles.map {
                    it.copy(isActive = it.id == vehicleId)
                }
            )
        }
    }
}
