package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the HomeScreen — manages the list of vehicles.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    val vehicles: StateFlow<List<VehicleDisplayItem>> = vehicleRepository.getAllVehicles()
        .map { vehicles ->
            vehicles.map {
                VehicleDisplayItem(
                    id = it.id,
                    name = "${it.make} ${it.model}",
                    makeModel = "${it.make} • ${it.model}",
                    licensePlate = it.plateNumber,
                    odometerText = "${it.currentOdometer} km",
                    fuelTypes = it.fuelTypes,
                    isActive = false // This can be updated based on user interaction
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onEvent(event: AddEditVehiclePanelEvent) {
        when (event) {
            is AddEditVehiclePanelEvent.DeleteVehicleById -> deleteVehicleById(event.vehicleId)
            else -> Unit // Handle other events if necessary
        }
    }

    private fun deleteVehicleById(vehicleId: String) {
        viewModelScope.launch {
            vehicleRepository.deleteVehicleById(vehicleId)
        }
    }
}
