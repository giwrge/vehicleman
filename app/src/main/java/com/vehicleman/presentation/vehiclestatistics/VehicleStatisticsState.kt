package com.vehicleman.presentation.vehiclestatistics

import com.vehicleman.domain.util.CalculatedStats

data class VehicleStatisticsState(
    val vehicleName: String = "",
    val stats: CalculatedStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
