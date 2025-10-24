package com.vehicleman.domain.use_case

import javax.inject.Inject

data class StatisticsUseCases @Inject constructor(
    val getVehicleStatistics: GetVehicleStatistics,
    val getVehicleConsumption: GetVehicleConsumption
)
