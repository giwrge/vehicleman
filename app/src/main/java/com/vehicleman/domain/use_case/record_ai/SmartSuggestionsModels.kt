package com.vehicleman.domain.use_case.record_ai

import java.util.Date
enum class SuggestionSource {
    RECENT_RECORD,
    DOMAIN_KEYWORD
}

data class SuggestionItem(
    val text: String,
    val source: SuggestionSource,
    val recordId: String? = null
)

data class SuggestionsRequest(
    val userQuery: String,
    val recentRecords: List<RecentRecordSuggestion>,
    val domainKeywords: List<String>,
    val maxSuggestions: Int = 8
)

data class RecentRecordSuggestion(
    val recordId: String,
    val title: String,
    val description: String?,
    val date: Date
)
