package com.vehicleman.presentation.statisticvehicle

import com.vehicleman.domain.util.CalculatedStats

data class StatisticVehicleState(
    val vehicleName: String = "",
    val stats: CalculatedStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
