package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject
import kotlin.math.min

/**
 * Παράγει "έξυπνες" προτάσεις για τον τίτλο στο AddEditRecordScreen.
 * Συνδυάζει:
 *  - το κείμενο του χρήστη (query)
 *  - παλιές καταχωρήσεις (recentTitles / recentDescriptions)
 *  - domainKeywords (dictionary)
 *  - fuzzy matching score
 * Το ViewModel θα φιλτράρει ποιο suggestion θα κάνει apply όταν γίνει TAP.
 */
class GenerateSmartSuggestionsUseCase @Inject constructor() {

    operator fun invoke(request: SuggestionsRequest): List<SmartSuggestion> {
        val query = request.userQuery.trim().lowercase()
        if (query.isEmpty()) return emptyList()

        val candidates = mutableListOf<SmartSuggestion>()

        // 1) Από πρόσφατους τίτλους
        for (t in request.recentTitles) {
            val score = similarity(query, t.lowercase())
            if (score > 0.25) {
                candidates += SmartSuggestion(text = t, score = score)
            }
        }

        // 2) Από πρόσφατες περιγραφές
        for (desc in request.recentDescriptions) {
            val score = similarity(query, desc.lowercase())
            if (score > 0.25) {
                val short = desc.take(40) + if (desc.length > 40) "…" else ""
                candidates += SmartSuggestion(text = short, score = score * 0.8)
            }
        }

        // 3) Από domain keywords (dictionary)
        for (keyword in request.domainKeywords) {
            val score = similarity(query, keyword.lowercase())
            if (score > 0.35) {
                candidates += SmartSuggestion(text = keyword, score = score * 1.1)
            }
        }

        // 4) Extra BOOST για suggestions που ξεκινούν με το query
        val boosted = candidates.map {
            if (it.text.lowercase().startsWith(query)) {
                it.copy(score = it.score + 0.2)
            } else it
        }

        // 5) Sorting by score desc, unique text, limit
        return boosted
            .sortedByDescending { it.score }
            .distinctBy { it.text.lowercase() }
            .take(request.maxSuggestions)
    }

    /**
     * Fuzzy matching: normalized similarity score (0.0 – 1.0)
     * Χρησιμοποιούμε μια light μορφή Levenshtein.
     */
    private fun similarity(a: String, b: String): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        if (a == b) return 1.0

        val maxLen = maxOf(a.length, b.length)
        val distance = levenshtein(a, b)

        val score = 1.0 - (distance.toDouble() / maxLen.toDouble())
        return score.coerceIn(0.0, 1.0)
    }

    /**
     * Classic Levenshtein Distance – O(n*m) αλλά εδώ είναι μικρά strings → fine.
     */
    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}

/**
 * Τι δίνουμε σαν είσοδο στο UseCase.
 */
data class SuggestionsRequest(
    val userQuery: String,
    val recentTitles: List<String>,
    val recentDescriptions: List<String>,
    val domainKeywords: List<String>,
    val maxSuggestions: Int = 8
)

/**
 * Τι επιστρέφει.
 */
data class SmartSuggestion(
    val text: String,
    val score: Double
)
