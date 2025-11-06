package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle

data class StatisticsState(
    val drivers: List<Driver> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToDriverStatistics: String? = null,
    val navigateToVehicleStatistics: String? = null
)
