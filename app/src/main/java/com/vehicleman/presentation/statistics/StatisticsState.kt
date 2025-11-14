package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.Driver
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem // Import VehicleDisplayItem

data class StatisticsState(
    val drivers: List<Driver> = emptyList(),
    val vehicles: List<VehicleDisplayItem> = emptyList(), // Changed to VehicleDisplayItem
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToDriverStatistics: String? = null,
    val navigateToVehicleStatistics: String? = null
)
