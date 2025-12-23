package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.Record

data class SuggestionsRequest(
    val userQuery: String,
    val recentRecords: List<Record>,
    val domainKeywords: List<String>,
    val maxSuggestions: Int = 8
)
