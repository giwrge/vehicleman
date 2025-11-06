package com.vehicleman.presentation.statisticsdriver

import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.util.CalculatedStats

data class VehicleWithStats(
    val vehicle: Vehicle,
    val stats: CalculatedStats
)

data class DriverStatisticsState(
    val driverName: String = "",
    val vehicleStats: List<VehicleWithStats> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
