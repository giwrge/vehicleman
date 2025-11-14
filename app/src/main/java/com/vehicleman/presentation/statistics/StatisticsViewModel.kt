package com.vehicleman.presentation.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.data.mappers.toDriver
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    driverRepository: DriverRepository,
    vehicleRepository: VehicleRepository,
    userPreferencesRepository: UserPreferencesRepository // Add UserPreferencesRepository
) : ViewModel() {

    // Combine all necessary data sources, including the user
    val state: StateFlow<StatisticsState> = combine(
        driverRepository.getDriversWithVehicles().map { list -> list.map { it.driver.toDriver() } },
        vehicleRepository.getAllVehicles(),
        userPreferencesRepository.user // Listen to user changes
    ) { drivers, vehicles, currentUser ->
        
        // 1. Filter vehicles based on user role (same logic as HomeViewModel)
        val visibleVehicles = if (currentUser.twinAppRole == TwinAppRole.SUB_DRIVER && currentUser.subDriverType == SubDriverType.SINGLE) {
            vehicles.filter { currentUser.assignedVehicleIds.contains(it.id) } // Removed redundant elvis operator
        } else {
            vehicles
        }

        // 2. Map the visible vehicles to VehicleDisplayItem for UI safety
        val vehicleDisplayItems = visibleVehicles.map { it.toVehicleDisplayItem() }

        Log.d("StatisticsViewModel", "Loaded ${drivers.size} drivers")
        Log.d("StatisticsViewModel", "Loaded ${vehicleDisplayItems.size} vehicles")

        // 3. Update the state with the safe-to-display list
        StatisticsState(drivers = drivers, vehicles = vehicleDisplayItems)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsState(isLoading = true)
    )

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.OnDriverClick -> {
                _navigationState.update { it.copy(navigateToDriverStatistics = event.driver.driverId) }
            }
            is StatisticsEvent.OnVehicleClick -> {
                // Now using VehicleDisplayItem, so we get the id from it
                _navigationState.update { it.copy(navigateToVehicleStatistics = event.vehicle.id) }
            }
            is StatisticsEvent.OnSortVehiclesClick -> {
                // Handle vehicle sorting
            }
            is StatisticsEvent.NavigationHandled -> {
                _navigationState.update { NavigationState() } // Reset navigation state
            }
        }
    }
}

// Helper function to map Vehicle to VehicleDisplayItem
private fun Vehicle.toVehicleDisplayItem(): VehicleDisplayItem {
    return VehicleDisplayItem(
        id = this.id,
        name = "${this.make ?: ""} ${this.model ?: ""}",
        makeModel = "${this.make ?: ""} • ${this.model ?: ""}",
        licensePlate = this.plateNumber ?: "",
        odometerText = "${this.currentOdometer ?: 0} km",
        fuelTypes = this.fuelTypes ?: emptyList(),
        isActive = false
    )
}

// Separate state for navigation to avoid re-triggering recomposition of the main screen
data class NavigationState(
    val navigateToDriverStatistics: String? = null,
    val navigateToVehicleStatistics: String? = null
)