package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the HomeScreen — manages the list of vehicles.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val user: StateFlow<User> = userPreferencesRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    val vehicles: StateFlow<List<VehicleDisplayItem>> = combine(
        vehicleRepository.getAllVehicles(),
        userPreferencesRepository.vehicleSortOrder,
        userPreferencesRepository.customVehicleOrder,
        user // Use the user flow from above
    ) { vehicles, sortOrder, customOrder, currentUser ->
        // 1. Filter vehicles based on user role
        val visibleVehicles = if (currentUser.twinAppRole == TwinAppRole.SUB_DRIVER && currentUser.subDriverType == SubDriverType.SINGLE) {
            vehicles.filter { currentUser.assignedVehicleIds.contains(it.id) }
        } else {
            vehicles
        }

        // 2. Sort the visible vehicles
        val sortedVehicles = when (sortOrder) {
            VehicleSortOrder.ALPHABETICAL -> visibleVehicles.sortedBy { it.make }
            VehicleSortOrder.BY_DATE_ADDED -> visibleVehicles.sortedBy { it.dateAdded }
            VehicleSortOrder.MOST_ENTRIES -> visibleVehicles.sortedByDescending { it.recordCount }
            VehicleSortOrder.BY_LAST_MODIFIED -> visibleVehicles.sortedByDescending { it.lastModified }
            VehicleSortOrder.CUSTOM -> {
                if (customOrder.isNotBlank()) {
                    val customOrderIds = customOrder.split(",")
                    visibleVehicles.sortedBy { vehicle ->
                        customOrderIds.indexOf(vehicle.id).let { if (it == -1) Int.MAX_VALUE else it }
                    }
                } else {
                    visibleVehicles
                }
            }
        }

        // 3. Map to display items
        sortedVehicles.map {
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