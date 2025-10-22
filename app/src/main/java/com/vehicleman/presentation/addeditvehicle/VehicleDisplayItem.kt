package com.vehicleman.presentation.addeditvehicle

data class VehicleDisplayItem(
    val id: String,
    val name: String,
    val makeModel: String,
    val licensePlate: String,
    val odometerText: String,
    val fuelType: String,
    val isActive: Boolean = false
)
