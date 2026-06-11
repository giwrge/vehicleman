package com.vehicleman.presentation.statistics

import com.vehicleman.domain.model.DetailedStatistics
import com.vehicleman.domain.model.TimeFilter

data class DetailedAnalysisState(
    val statistics: DetailedStatistics? = null,
    val isLoading: Boolean = false,
    val timeFilter: TimeFilter = TimeFilter.SUMMARY,
    val title: String = "",
    val error: String? = null
)
