package com.vehicleman.presentation.statisticvehicle

data class StatisticVehicleState(
    val totalConsumption: Double = 0.0,
    val consumptionPerFuelType: Map<String, Double> = emptyMap()
)
