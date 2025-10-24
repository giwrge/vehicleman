package com.vehicleman.presentation.statistics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.use_case.StatisticsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsUseCases: StatisticsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(StatisticsState())
    val state: State<StatisticsState> = _state

    init {
        getVehicleStatistics()
    }

    private fun getVehicleStatistics() {
        statisticsUseCases.getVehicleStatistics()
            .onEach { stats ->
                _state.value = state.value.copy(vehicleStatistics = stats)
            }
            .launchIn(viewModelScope)
    }
}
