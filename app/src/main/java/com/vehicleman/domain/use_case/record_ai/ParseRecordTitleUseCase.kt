package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject

/**
 * Πολύ απλός parser του τίτλου που προσπαθεί να βρει:
 * - ποσότητες (lt)
 * - κόστος (€)
 * - τιμή €/lt
 * - fuel keywords
 */
class ParseRecordTitleUseCase @Inject constructor() {

    operator fun invoke(title: String): ParsedInput {
        val lower = title.lowercase()

        val numbers = "\\d+(\\.\\d+)?".toRegex()
            .findAll(lower)
            .map { it.value.toDouble() }
            .toList()

        var cost: Double? = null
        var liters: Double? = null
        var pricePerLiter: Double? = null
        var fuelType: String? = null
        var isFuel = false

        // ---- Fuel type detection ----
        if (lower.contains("100")) fuelType = "unleaded_100"
        if (lower.contains("98")) fuelType = "unleaded_98"
        if (lower.contains("95")) fuelType = "unleaded_95"
        if (lower.contains("diesel")) fuelType = "diesel"
        if (lower.contains("dizel")) fuelType = "diesel"
        if (lower.contains("βενζ") || lower.contains("benz")) fuelType = "unleaded_95"

        // Keywords indicating fuel
        if (lower.contains("fuel") ||
            lower.contains("gas") ||
            lower.contains("benz") ||
            lower.contains("βενζ") ||
            lower.contains("full") ||
            lower.contains("tank")
        ) {
            isFuel = true
        }

        // ---- Cost parsing ( € or eur ) ----
        val euroMatch = "(\\d+(\\.\\d+)?)\\s?(€|eur|e)".toRegex().find(lower)
        if (euroMatch != null) {
            cost = euroMatch.groupValues[1].toDouble()
        } else if (numbers.isNotEmpty()) {
            // If fuel-like and only one number → assume cost
            if (isFuel && numbers.size == 1) {
                cost = numbers.first()
            }
        }

        // ---- Liters parsing (lt) ----
        val ltMatch = "(\\d+(\\.\\d+)?)\\s?(lt|λίτρα|λιτρα)".toRegex().find(lower)
        if (ltMatch != null) {
            liters = ltMatch.groupValues[1].toDouble()
        }

        // ---- €/lt parsing ----
        val ppuMatch = "(\\d+(\\.\\d+)?)/(lt|λίτρο)".toRegex().find(lower)
        if (ppuMatch != null) {
            pricePerLiter = ppuMatch.groupValues[1].toDouble()
        }

        return ParsedInput(
            rawTitle = title,
            numbers = numbers,
            cost = cost,
            liters = liters,
            pricePerLiter = pricePerLiter,
            fuelTypeKeyword = fuelType,
            isFuelLike = isFuel
        )
    }
}
