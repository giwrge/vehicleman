package com.vehicleman.presentation.vehicle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.util.CalculatedStats
import com.vehicleman.domain.util.VehicleStatisticsCalculator
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticVehicleState(
    val vehicle: Vehicle? = null,
    val stats: CalculatedStats = CalculatedStats(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticVehicleViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val vehicleRepository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = savedStateHandle[NavDestinations.VEHICLE_ID_KEY]!!

    private val _state = MutableStateFlow(StatisticVehicleState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            _state.value = _state.value.copy(vehicle = vehicle)
            
            recordRepository.getRecordsForVehicle(vehicleId).collect { records ->
                val calculatedStats = VehicleStatisticsCalculator.calculate(records)
                _state.value = _state.value.copy(
                    stats = calculatedStats,
                    isLoading = false
                )
            }
        }
    }
}