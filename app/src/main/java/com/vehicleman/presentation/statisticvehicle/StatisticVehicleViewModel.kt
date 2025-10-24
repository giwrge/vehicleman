package com.vehicleman.presentation.statisticvehicle

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.use_case.StatisticsUseCases
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StatisticVehicleViewModel @Inject constructor(
    private val statisticsUseCases: StatisticsUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(StatisticVehicleState())
    val state: State<StatisticVehicleState> = _state

    private val vehicleId: String = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)!!

    init {
        getVehicleConsumption(vehicleId)
    }

    private fun getVehicleConsumption(vehicleId: String) {
        statisticsUseCases.getVehicleConsumption(vehicleId)
            .onEach { consumption ->
                _state.value = state.value.copy(
                    totalConsumption = consumption.totalConsumption,
                    consumptionPerFuelType = consumption.consumptionPerFuelType
                )
            }
            .launchIn(viewModelScope)
    }
}
