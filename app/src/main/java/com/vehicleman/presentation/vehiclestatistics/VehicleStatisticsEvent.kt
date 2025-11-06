package com.vehicleman.presentation.vehiclestatistics

sealed class VehicleStatisticsEvent {
    object Refresh : VehicleStatisticsEvent()
}
