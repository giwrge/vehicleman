package com.vehicleman.presentation.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.data.mappers.toDriver
import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.ui.navigation.NavDestinations
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
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val state: StateFlow<StatisticsState> = combine(
        driverRepository.getDriversWithVehicles().map { list -> list.map { it.driver.toDriver() } },
        vehicleRepository.getAllVehicles(),
        userPreferencesRepository.user
    ) { drivers, vehicles, currentUser ->
        
        val visibleVehicles = if (currentUser.twinAppRole == TwinAppRole.SUB_DRIVER && currentUser.subDriverType == SubDriverType.SINGLE) {
            vehicles.filter { currentUser.assignedVehicleIds.contains(it.id) }
        } else {
            vehicles
        }

        val vehicleDisplayItems = visibleVehicles.map { it.toVehicleDisplayItem() }

        // Construct Users list for statistics (Main User + Drivers)
        // Main User is virtual and represents all data
        val mainUser = Driver(driverId = "main_user", name = "Main User")
        val allUsers = listOf(mainUser) + drivers

        StatisticsState(
            drivers = allUsers,
            vehicles = vehicleDisplayItems,
            isLoading = false
        )

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
                val route = NavDestinations.detailedAnalysisRoute(
                    type = NavDestinations.TYPE_USER,
                    id = event.driver.driverId
                )
                _navigationState.update { it.copy(navigateToDetailedAnalysis = route) }
            }
            is StatisticsEvent.OnVehicleClick -> {
                val route = NavDestinations.detailedAnalysisRoute(
                    type = NavDestinations.TYPE_VEHICLE,
                    id = event.vehicle.id
                )
                _navigationState.update { it.copy(navigateToDetailedAnalysis = route) }
            }
            is StatisticsEvent.NavigationHandled -> {
                _navigationState.update { NavigationState() }
            }
            else -> {}
        }
    }
}

private fun Vehicle.toVehicleDisplayItem(): VehicleDisplayItem {
    return VehicleDisplayItem(
        id = this.id,
        name = "${this.make} ${this.model}",
        makeModel = "${this.make} • ${this.model}",
        licensePlate = this.plateNumber,
        odometerText = "${this.currentOdometer} km",
        fuelTypes = this.fuelTypes,
        isActive = false
    )
}

data class NavigationState(
    val navigateToDetailedAnalysis: String? = null
)
