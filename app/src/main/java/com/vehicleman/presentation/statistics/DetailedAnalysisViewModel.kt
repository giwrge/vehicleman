package com.vehicleman.presentation.statistics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.DetailedStatistics
import com.vehicleman.domain.model.TimeFilter
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.GetDetailedStatisticsUseCase
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedAnalysisViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDetailedStatistics: GetDetailedStatisticsUseCase,
    private val vehicleRepository: VehicleRepository,
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val type: String = savedStateHandle[NavDestinations.ANALYSIS_TYPE_KEY] ?: ""
    private val id: String = savedStateHandle[NavDestinations.ANALYSIS_ID_KEY] ?: ""

    private val _timeFilter = MutableStateFlow(TimeFilter.SUMMARY)
    
    private val _title = MutableStateFlow("Analysis")
    val title: StateFlow<String> = _title

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DetailedAnalysisState> = _timeFilter.flatMapLatest { filter ->
        flow {
            emit(DetailedAnalysisState(isLoading = true, timeFilter = filter))
            
            val statsFlow = if (type == NavDestinations.TYPE_VEHICLE) {
                val vehicle = vehicleRepository.getVehicleById(id)
                _title.value = vehicle?.name ?: "Vehicle"
                getDetailedStatistics(id, filter)
            } else if (id == "main_user") {
                _title.value = "Main User (All Vehicles)"
                val allVehicles = vehicleRepository.getVehicles().map { it.id }
                getDetailedStatistics(allVehicles, filter)
            } else {
                // Specific driver - need to find vehicles assigned to them
                val driversWithVehicles = driverRepository.getDriversWithVehicles().first()
                val driverData = driversWithVehicles.find { it.driver.driverId == id }
                _title.value = driverData?.driver?.name ?: "Driver"
                val vehicleIds = driverData?.vehicles?.map { it.id } ?: emptyList()
                getDetailedStatistics(vehicleIds, filter)
            }

            statsFlow.collect { stats ->
                emit(DetailedAnalysisState(statistics = stats, isLoading = false, timeFilter = filter))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailedAnalysisState(isLoading = true)
    )

    fun onTimeFilterSelected(filter: TimeFilter) {
        _timeFilter.value = filter
    }
}
