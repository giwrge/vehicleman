package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.VehicleStatistics

data class StatisticsState(
    val vehicleStatistics: List<VehicleStatistics> = emptyList()
)
