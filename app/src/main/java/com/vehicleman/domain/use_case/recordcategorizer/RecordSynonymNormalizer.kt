package com.vehicleman.domain.use_case.recordcategorizer

import com.vehicleman.domain.model.category.RecordCategory
import java.text.Normalizer
import java.util.Locale
import kotlin.math.min

/**
 * Normalizer + matcher που ενώνει title + description,
 * καθαρίζει το κείμενο και ψάχνει στο RecordSynonymDictionary.
 */
object RecordSynonymNormalizer {

    private val dictionary = RecordSynonymDictionary.keywordToCategory

    /**
     * Προσπαθεί να εντοπίσει κατηγορία με βάση:
     *  - title
     *  - description (αν υπάρχει)
     *
     * Επιστρέφει null αν δεν βρεθεί κάτι (ώστε να συνεχίσει
     * ο RecordCategorizerUseCase με επιπλέον κανόνες/ fallback).
     */
    fun detectCategory(title: String?, description: String?, isReminder: Boolean): RecordCategory? {
        val raw = (title.orEmpty() + " " + description.orEmpty()).trim()
        if (raw.isEmpty()) return null

        val normalized = normalize(raw)

        // 1) Multi-word keys πρώτα (π.χ. "oil change", "air filter", "wheel alignment")
        val multiWordKeys = dictionary.keys.filter { it.contains(' ') }
            .sortedByDescending { it.length }

        for (key in multiWordKeys) {
            if (normalized.contains(key)) {
                return dictionary[key]
            }
        }

        // 2) Single-token match
        val tokens = normalized
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        tokens.forEach { token ->
            dictionary[token]?.let { return it }
        }

        // 3) Πολύ light fuzzy matching για μικρο-λάθη (edit distance <= 1)
        tokens.forEach { token ->
            val match = findCloseKeyword(token)
            if (match != null) return dictionary[match]
        }

        return null
    }

    // -------------------------------------------------------

    private fun normalize(input: String): String {
        val noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

        return noAccents
            .lowercase(Locale.getDefault())
            .replace("[^a-z0-9α-ω ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    /**
     * Πολύ απλός fuzzy matcher: για κάθε token, βρίσκει keyword
     * με απόσταση Levenshtein <= 1 (για μικρά typos).
     */
    private fun findCloseKeyword(token: String): String? {
        // Αν είναι πολύ μικρή ή μεγάλη λέξη, αγνόησέ τη
        if (token.length < 3 || token.length > 20) return null

        var bestKey: String? = null
        var bestDistance = Int.MAX_VALUE

        for (key in dictionary.keys) {
            val d = levenshteinDistance(token, key)
            if (d < bestDistance) {
                bestDistance = d
                bestKey = key
            }
            if (bestDistance <= 1) break
        }

        return if (bestDistance <= 1) bestKey else null
    }

    // Very small Levenshtein implementation for fuzzy matching
    private fun levenshteinDistance(a: String, b: String): Int {
        val la = a.length
        val lb = b.length
        if (la == 0) return lb
        if (lb == 0) return la

        val dp = IntArray(lb + 1) { it }

        for (i in 1..la) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..lb) {
                val temp = dp[j]
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[j] = min(
                    min(
                        dp[j] + 1,      // deletion
                        dp[j - 1] + 1   // insertion
                    ),
                    prev + cost        // substitution
                )
                prev = temp
            }
        }

        return dp[lb]
    }
}

