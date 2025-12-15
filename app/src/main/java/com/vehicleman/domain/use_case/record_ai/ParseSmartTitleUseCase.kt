package com.vehicleman.domain.use_case.record_ai

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
 * - Βλέπει λέξεις για service, ασφάλεια, ΚΤΕΟ, λάστιχα κ.λπ.
 * - Αναγνωρίζει ημερομηνίες (κυρίως μορφές dd/MM/yyyy και dd-MM-yyyy)
 */



class ParseSmartTitleUseCase @Inject constructor() {

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

        // --- Fuel detection ---
        val fuelTypeHint = detectFuelType(lower)

        // --- Expense / service keywords ---
        val (categoryHint, serviceItems) = detectCategoryAndServiceItems(lower)

        // --- Amounts & fuel numbers ---
        val costEuro = detectFirstDouble(euroRegex, lower)
        val liters = detectFirstDouble(litersRegex, lower)
        val pricePerLiter = detectFirstDouble(pricePerLiterRegex, lower)

        // --- Date / reminder style detection ---
        val detectedDate = detectDate(lower)
        val isFutureDate = detectedDate?.isAfter(nowDate) == true

        val isFuelLike = fuelTypeHint != null || containsAny(lower, FUEL_KEYWORDS)
        val isReminderLike = isFutureDate || containsAny(lower, REMINDER_KEYWORDS)
        val isExpenseLike = !isFuelLike && (categoryHint != null || containsAny(lower, EXPENSE_KEYWORDS))

        return ParsedSmartTitle(
            raw = rawTitle,
            normalized = normalized,
            tokens = tokens,
            isFuelLike = isFuelLike,
            isExpenseLike = isExpenseLike,
            isReminderLike = isReminderLike,
            categoryHint = categoryHint,
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

    private fun detectCategoryAndServiceItems(lower: String): Pair<CategoryHint?, List<String>> {
        val items = mutableListOf<String>()
        var hint: CategoryHint? = null

        if (containsAny(lower, SERVICE_KEYWORDS)) {
            hint = CategoryHint.SERVICE
            if (lower.contains("λαδια") || lower.contains("ladia")) items += "Λάδια"
            if (lower.contains("φιλτρο") || lower.contains("filtr")) items += "Φίλτρο"
            if (lower.contains("μπουζ") || lower.contains("bouz")) items += "Μπουζί"
            if (lower.contains("φιλτρο αερο") || lower.contains("air filter")) items += "Φίλτρο αέρος"
        }

        if (containsAny(lower, TIRE_KEYWORDS)) {
            hint = CategoryHint.TIRES
        }

        if (containsAny(lower, INSURANCE_KEYWORDS)) {
            hint = CategoryHint.INSURANCE
        }

        if (containsAny(lower, TAX_KEYWORDS)) {
            hint = CategoryHint.TAXES
        }

        if (containsAny(lower, WASH_KEYWORDS)) {
            hint = CategoryHint.WASH
        }

        return hint to items
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

        private val TIRE_KEYWORDS = listOf(
            "λαστιχα", "lastixa", "ελαστικ", "pneumat", "tyre", "tire"
        )

        private val INSURANCE_KEYWORDS = listOf(
            "ασφαλεια", "asfal", "insurance"
        )

        private val TAX_KEYWORDS = listOf(
            "τελη", "teli", "κυκλοφοριας", "road tax"
        )

        private val WASH_KEYWORDS = listOf(
            "πλυσιμο", "plisimo", "car wash", "πλυντηριο"
        )

        private val REMINDER_KEYWORDS = listOf(
            "kteo", "κτεο", "service", "ασφαλεια", "τελη", "ελεγχος", "inspection", "υπενθυμιση"
        )

        private val EXPENSE_KEYWORDS = listOf(
            "service", "σερβις", "λαστιχα", "ασφαλεια", "φιλτρο", "μπουζι", "πλυσιμο", "πισω φρεν"
        )
    }
}
