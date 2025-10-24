package com.vehicleman.domain.model

data class VehicleConsumption(
    val totalConsumption: Double,
    val consumptionPerFuelType: Map<String, Double>
)
