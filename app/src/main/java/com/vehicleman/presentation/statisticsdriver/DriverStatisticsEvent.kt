package com.vehicleman.presentation.statisticsdriver

sealed class DriverStatisticsEvent {
    object Refresh : DriverStatisticsEvent()
}
