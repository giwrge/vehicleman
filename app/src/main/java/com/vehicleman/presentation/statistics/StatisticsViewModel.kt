package com.vehicleman.presentation.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val drivers = driverRepository.getAllDriversList()
                val vehicles = vehicleRepository.getAllVehiclesList()
                Log.d("StatisticsViewModel", "Loaded ${drivers.size} drivers")
                Log.d("StatisticsViewModel", "Loaded ${vehicles.size} vehicles")
                _state.update { it.copy(drivers = drivers, vehicles = vehicles, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load data: ${e.message}", isLoading = false) }
                Log.e("StatisticsViewModel", "Error loading data", e)
            }
        }
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.OnDriverClick -> {
                Log.d("StatisticsViewModel", "Driver clicked: ${event.driver.driverId}")
                _state.update { it.copy(navigateToDriverStatistics = event.driver.driverId) }
            }
            is StatisticsEvent.OnVehicleClick -> {
                Log.d("StatisticsViewModel", "Vehicle clicked: ${event.vehicle.id}")
                _state.update { it.copy(navigateToVehicleStatistics = event.vehicle.id) }
            }
            is StatisticsEvent.OnSortVehiclesClick -> {
                // Handle vehicle sorting
            }
            is StatisticsEvent.NavigationHandled -> {
                _state.update { it.copy(navigateToDriverStatistics = null, navigateToVehicleStatistics = null) }
            }
        }
    }
}
