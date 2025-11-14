package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.Driver
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem // Import VehicleDisplayItem

sealed class StatisticsEvent {
    data class OnDriverClick(val driver: Driver) : StatisticsEvent()
    data class OnVehicleClick(val vehicle: VehicleDisplayItem) : StatisticsEvent() // Changed to VehicleDisplayItem
    object OnSortVehiclesClick : StatisticsEvent()
    object NavigationHandled : StatisticsEvent() // Added this event
}
