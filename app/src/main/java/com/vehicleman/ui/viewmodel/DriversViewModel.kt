package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.data.entities.DriverWithVehicles
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriversScreenState(
    val drivers: List<DriverWithVehicles> = emptyList(),
    val allVehicles: List<Vehicle> = emptyList(),
    val newDriverName: String = "",
    val expandedDriverId: String? = null
)

@HiltViewModel
class DriversViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriversScreenState())
    val uiState: StateFlow<DriversScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                driverRepository.getDriversWithVehicles(),
                vehicleRepository.getAllVehicles()
            ) { drivers, vehicles ->
                _uiState.value = _uiState.value.copy(
                    drivers = drivers,
                    allVehicles = vehicles
                )
            }.collect{}
        }
    }

    fun onNewDriverNameChange(name: String) {
        _uiState.value = _uiState.value.copy(newDriverName = name)
    }

    fun onAddDriver() {
        viewModelScope.launch {
            if (_uiState.value.newDriverName.isNotBlank()) {
                driverRepository.addDriver(_uiState.value.newDriverName)
                _uiState.value = _uiState.value.copy(newDriverName = "")
            }
        }
    }

    fun onDeleteDriver(driverId: String) {
        viewModelScope.launch {
            driverRepository.deleteDriver(driverId)
        }
    }

    fun onVehicleCheckedChange(driverId: String, vehicleId: String, isChecked: Boolean) {
        viewModelScope.launch {
            if (isChecked) {
                driverRepository.assignVehicleToDriver(driverId, vehicleId)
            } else {
                driverRepository.unassignVehicleFromDriver(driverId, vehicleId)
            }
        }
    }
    
    fun onDriverExpanded(driverId: String) {
        val newExpandedId = if (_uiState.value.expandedDriverId == driverId) null else driverId
        _uiState.value = _uiState.value.copy(expandedDriverId = newExpandedId)
    }
}