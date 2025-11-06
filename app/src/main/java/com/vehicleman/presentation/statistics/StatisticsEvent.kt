package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle

sealed class StatisticsEvent {
    data class OnDriverClick(val driver: Driver) : StatisticsEvent()
    data class OnVehicleClick(val vehicle: Vehicle) : StatisticsEvent()
    object OnSortVehiclesClick : StatisticsEvent()
    object NavigationHandled : StatisticsEvent() // Added this event
}
