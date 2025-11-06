package com.vehicleman.presentation.statisticsdriver

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.data.mappers.toVehicle
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.util.VehicleStatisticsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverStatisticsViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val recordRepository: RecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val driverId: String = savedStateHandle.get<String>("driverId")!!

    private val _state = MutableStateFlow(DriverStatisticsState())
    val state: StateFlow<DriverStatisticsState> = _state

    init {
        loadStatistics()
    }

    fun onEvent(event: DriverStatisticsEvent) {
        when (event) {
            DriverStatisticsEvent.Refresh -> loadStatistics()
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val driverWithVehicles = driverRepository.getDriversWithVehicles().first()
                    .find { it.driver.driverId == driverId }

                if (driverWithVehicles == null) {
                    _state.update { it.copy(error = "Driver not found", isLoading = false) }
                    return@launch
                }

                val stats = driverWithVehicles.vehicles.map { vehicle ->
                    val records = recordRepository.getRecordsForVehicle(vehicle.id).first()
                    val calculatedStats = VehicleStatisticsCalculator.calculate(records)
                    VehicleWithStats(vehicle.toVehicle(), calculatedStats)
                }

                _state.update {
                    it.copy(
                        driverName = driverWithVehicles.driver.name,
                        vehicleStats = stats,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load statistics: ${e.message}", isLoading = false) }
            }
        }
    }
}
