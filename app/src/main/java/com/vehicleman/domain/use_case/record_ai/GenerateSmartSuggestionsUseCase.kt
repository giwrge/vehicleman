package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject
import kotlin.math.min

class GenerateSmartSuggestionsUseCase @Inject constructor() {

    operator fun invoke(request: SuggestionsRequest): List<SmartSuggestion> {
        val q = request.userQuery.trim().lowercase()
        if (q.isBlank()) return emptyList()

        val pool = buildList {
            addAll(request.recentTitles)
            addAll(request.domainKeywords)
            // Προαιρετικά: recentDescriptions (σαν έξτρα “έμπνευση”)
            addAll(request.recentDescriptions)
        }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        val scored = pool.map { text ->
            val score = score(q, text.lowercase())
            SmartSuggestion(text = text, score = score)
        }
            .filter { it.score > 0.20 }
            .sortedByDescending { it.score }
            .take(request.maxSuggestions)

        return scored
    }

    private fun score(q: String, candidate: String): Double {
        if (candidate == q) return 1.0
        if (candidate.startsWith(q)) return 0.95
        if (candidate.contains(q)) return 0.75

        // πολύ light fuzzy: κοινά tokens
        val qTokens = q.split(" ").filter { it.isNotBlank() }.toSet()
        val cTokens = candidate.split(" ").filter { it.isNotBlank() }.toSet()
        if (qTokens.isEmpty() || cTokens.isEmpty()) return 0.0

        val inter = qTokens.intersect(cTokens).size
        val union = qTokens.union(cTokens).size
        return inter.toDouble() / union.toDouble()
    }
}

/** input για suggestions */
data class SuggestionsRequest(
    val userQuery: String,
    val recentTitles: List<String>,
    val recentDescriptions: List<String>,
    val domainKeywords: List<String>,
    val maxSuggestions: Int = 8
)

/** suggestion item */
data class SmartSuggestion(
    val text: String,
    val score: Double
)
