package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject

/**
 * Use case που κάνει "έξυπνο" parsing του τίτλου:
 * - Καθαρίζει ελληνικά/greeklish → normalized tokens
 * - Αναγνωρίζει καύσιμα (benzini, diesel, 95, 100 κτλ.)
 * - Βρίσκει ποσά σε €
 * - Βρίσκει λίτρα (lt)
 * - Βρίσκει τιμή €/lt (1.719, 1,719 κτλ.)
 * - **Χρησιμοποιεί το RecordCategorizerUseCase για να βρει την κατηγορία**
 * - Αναγνωρίζει ημερομηνίες (κυρίως μορφές dd/MM/yyyy και dd-MM-yyyy)
 */
class ParseSmartTitleUseCase @Inject constructor(
    private val recordCategorizer: RecordCategorizerUseCase
) {

    private val euroRegex = Regex("""(\d+[.,]?\d*)\s*(e|€)""", RegexOption.IGNORE_CASE)
    private val litersRegex = Regex("""(\d+[.,]?\d*)\s*(lt|λίτρα?)""", RegexOption.IGNORE_CASE)
    private val pricePerLiterRegex = Regex("""(\d+[.,]\d{2,3})\s*(€/lt|/lt|ευρώ/lt)?""", RegexOption.IGNORE_CASE)

    // Πολύ basic date parsing: 12/12/2025, 12-12-2025 κ.λπ.
    private val datePatterns = listOf(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    )

    operator fun invoke(rawTitle: String, nowDate: LocalDate = LocalDate.now()): ParsedSmartTitle {
        val normalized = normalize(rawTitle)
        val tokens = normalized.split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        val lower = normalized.lowercase(Locale.getDefault())

        // --- Date / reminder style detection ---
        val detectedDate = detectDate(lower)
        val isFutureDate = detectedDate?.isAfter(nowDate) == true

        // --- Main Category Detection (using the injected use case) ---
        val detectedCategory = recordCategorizer(
            title = rawTitle,
            isReminder = isFutureDate
        )

        // --- Fuel specific detection ---
        val fuelTypeHint = detectFuelType(lower)

        // --- Amounts & fuel numbers ---
        val costEuro = detectFirstDouble(euroRegex, lower)
        val liters = detectFirstDouble(litersRegex, lower)
        val pricePerLiter = detectFirstDouble(pricePerLiterRegex, lower)

        // --- Flags based on the new, accurate category ---
        val isFuelLike = detectedCategory is RecordCategory.ExpenseCategory.Fuel || fuelTypeHint != null
        val isReminderLike = detectedCategory is RecordCategory.ReminderCategory || isFutureDate
        val isExpenseLike = detectedCategory is RecordCategory.ExpenseCategory && !isFuelLike

        // --- Simple service item detection (can be expanded) ---
        val serviceItems = detectServiceItems(lower)

        return ParsedSmartTitle(
            raw = rawTitle,
            normalized = normalized,
            tokens = tokens,
            isFuelLike = isFuelLike,
            isExpenseLike = isExpenseLike,
            isReminderLike = isReminderLike,
            detectedCategory = detectedCategory, // <-- The new, powerful category
            detectedFuelType = fuelTypeHint,
            detectedLiters = liters,
            detectedPricePerLiter = pricePerLiter,
            detectedCostEuro = costEuro,
            detectedGenericAmountEuro = costEuro, // προς το παρόν το ίδιο
            detectedServiceItems = serviceItems,
            detectedDate = detectedDate,
            isFutureDate = isFutureDate
        )
    }

    // ----------------- Helpers -----------------

    private fun normalize(input: String): String {
        val noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

        return noAccents
            .replace("€", "e")
            .replace(",", ",") // κρατάμε , όπως είναι, θα το χειριστούμε στο regex
            .trim()
    }

    private fun detectFuelType(lower: String): FuelTypeHint? {
        return when {
            containsAny(lower, listOf("100", "100ara", "unleaded 100")) &&
                    containsAny(lower, FUEL_KEYWORDS) -> FuelTypeHint.UNLEADED_100

            containsAny(lower, listOf("95", "95ara", "unleaded 95")) &&
                    containsAny(lower, FUEL_KEYWORDS) -> FuelTypeHint.UNLEADED_95

            containsAny(lower, listOf("diesel", "πετρελ", "gazol")) -> FuelTypeHint.DIESEL
            containsAny(lower, listOf("lpg", "υγραερι", "gas")) -> FuelTypeHint.LPG
            containsAny(lower, listOf("cng", "φυσικο αεριο")) -> FuelTypeHint.CNG
            containsAny(lower, listOf("recharge", "φορτιση", "φορτιση", "ηλεκτρ", "kw")) -> FuelTypeHint.ELECTRIC

            containsAny(lower, FUEL_KEYWORDS) -> FuelTypeHint.UNLEADED_95
            else -> null
        }
    }

    private fun detectServiceItems(lower: String): List<String> {
        val items = mutableListOf<String>()
        if (containsAny(lower, SERVICE_KEYWORDS)) {
            if (lower.contains("λαδια") || lower.contains("ladia")) items += "Λάδια"
            if (lower.contains("φιλτρο") || lower.contains("filtr")) items += "Φίλτρο"
            if (lower.contains("μπουζ") || lower.contains("bouz")) items += "Μπουζί"
            if (lower.contains("φιλτρο αερο") || lower.contains("air filter")) items += "Φίλτρο αέρος"
        }
        return items
    }

    private fun detectFirstDouble(regex: Regex, text: String): Double? {
        val match = regex.find(text) ?: return null
        val rawNumber = match.groupValues[1]
        val normalizedNumber = rawNumber.replace(",", ".")
        return normalizedNumber.toDoubleOrNull()
    }

    private fun detectDate(text: String): LocalDate? {
        // βρίσκουμε "κομμάτια" σαν 12/12/2025 ή 12-12-2025
        val dateCandidateRegex = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")
        val candidate = dateCandidateRegex.find(text)?.value ?: return null

        for (pattern in datePatterns) {
            try {
                return LocalDate.parse(candidate, pattern)
            } catch (ignored: DateTimeParseException) {
            }
        }
        return null
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }

    companion object {
        private val FUEL_KEYWORDS = listOf(
            "benzini", "benzin", "βενζιν", "fuel", "gasolina", "unleaded", "95", "100", "full tank"
        )

        private val SERVICE_KEYWORDS = listOf(
            "service", "σερβις", "λαδια", "ladia", "filtr", "φιλτρο", "bouzi", "μπουζι"
        )
    }
}
