package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.use_case.recordcategorizer.RecordSynonymNormalizer
import javax.inject.Inject

class GenerateSmartSuggestionsUseCase @Inject constructor() {

    operator fun invoke(request: SuggestionsRequest): List<SuggestionItem> {
        val qRaw = request.userQuery.trim()
        if (qRaw.isBlank()) return emptyList()

        val q = RecordSynonymNormalizer.normalizeForSearch(qRaw)
        if (q.isBlank()) return emptyList()

        val out = mutableListOf<SuggestionItem>()
        val seen = HashSet<String>()

        fun add(item: SuggestionItem) {
            val key = item.text.trim().lowercase()
            if (key.isBlank()) return
            if (seen.add(key)) out += item
        }

        // 1) RECENT: match normalized title/description αλλά επιστρέφω original title
        val recentSorted = request.recentRecords.sortedByDescending { it.date.time }
        for (r in recentSorted) {
            val titleNorm = RecordSynonymNormalizer.normalizeForSearch(r.title)
            val descNorm = RecordSynonymNormalizer.normalizeForSearch(r.description.orEmpty())

            if (titleNorm.contains(q) || descNorm.contains(q)) {
                add(
                    SuggestionItem(
                        text = r.title,
                        source = SuggestionSource.RECENT_RECORD,
                        recordId = r.recordId
                    )
                )
                if (out.size >= request.maxSuggestions) break
            }
        }

        // 2) DICT keywords
        if (out.size < request.maxSuggestions) {
            for (kw in request.domainKeywords) {
                val kwNorm = RecordSynonymNormalizer.normalizeForSearch(kw)
                if (kwNorm.contains(q)) {
                    add(
                        SuggestionItem(
                            text = kw,
                            source = SuggestionSource.DOMAIN_KEYWORD,
                            recordId = null
                        )
                    )
                    if (out.size >= request.maxSuggestions) break
                }
            }
        }

        return out.take(request.maxSuggestions)
    }
}
