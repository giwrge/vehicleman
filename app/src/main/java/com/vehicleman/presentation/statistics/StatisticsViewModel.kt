package com.vehicleman.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val drivers: List<Driver> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val selectedDriverId: String? = null, // null means "Main/All"
    val isSingleSubDriver: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                driverRepository.getDriversWithVehicles(),
                vehicleRepository.getAllVehicles(),
                userPreferencesRepository.vehicleSortOrder,
                userPreferencesRepository.customVehicleOrder,
                userPreferencesRepository.user
            ) { driversWithVehicles, allVehicles, sortOrder, customOrder, currentUser ->
                
                val isSingleSubDriver = currentUser.twinAppRole == TwinAppRole.SUB_DRIVER && currentUser.subDriverType == SubDriverType.SINGLE
                
                val drivers = if (isSingleSubDriver) emptyList() else driversWithVehicles.map { Driver(it.driver.driverId, it.driver.name) }
                
                val selectedDriver = driversWithVehicles.find { it.driver.driverId == _state.value.selectedDriverId }

                val visibleVehicles = when {
                    isSingleSubDriver -> allVehicles.filter { currentUser.assignedVehicleIds.contains(it.id) }
                    selectedDriver != null -> selectedDriver.vehicles.map { v -> allVehicles.find { it.id == v.id }!! } // Map back to full Vehicle objects
                    else -> allVehicles // "Main/All" shows all vehicles
                }

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

                _state.value = _state.value.copy(
                    drivers = drivers,
                    vehicles = sortedVehicles,
                    isSingleSubDriver = isSingleSubDriver,
                    isLoading = false
                )

            }.collect { }
        }
    }

    fun onDriverSelected(driverId: String?) {
        _state.value = _state.value.copy(selectedDriverId = driverId)
        // The `combine` flow will automatically recalculate the vehicles list
    }
}