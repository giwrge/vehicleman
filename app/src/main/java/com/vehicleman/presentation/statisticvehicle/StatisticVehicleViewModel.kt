package com.vehicleman.presentation.statisticvehicle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.util.VehicleStatisticsCalculator
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val recordRepository: RecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)!!

    private val _state = MutableStateFlow(StatisticVehicleState())
    val state: StateFlow<StatisticVehicleState> = _state

    init {
        loadStatistics()
    }

    fun onEvent(event: StatisticVehicleEvent) {
        when (event) {
            StatisticVehicleEvent.Refresh -> loadStatistics()
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle == null) {
                    _state.update { it.copy(error = "Vehicle not found", isLoading = false) }
                    return@launch
                }

                val records = recordRepository.getRecordsForVehicle(vehicleId).first()
                val stats = VehicleStatisticsCalculator.calculate(records)

                _state.update {
                    it.copy(
                        vehicleName = vehicle.name,
                        stats = stats,
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
