package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.model.VehicleStatistics

data class StatisticsState(
    val vehicleStatistics: List<VehicleStatistics> = emptyList(),
    val drivers: List<Driver> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val selectedDriverId: String? = null, // null means "Main/All"
    val isSingleSubDriver: Boolean = false,
    val isLoading: Boolean = true
)
