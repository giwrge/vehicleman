package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject

/**
 * Use case που μετατρέπει το ParsedSmartTitle σε "τι είδους record μοιάζει να είναι".
 *
 * Αυτό ΔΕΝ γνωρίζει τα δικά σου domain enums.
 * Απλά βγάζει ένα RecordTypeHint που μπορείς να χαρτογραφήσεις όπου θέλεις.
 */
class PredictRecordTypeFromParsedDataUseCase @Inject constructor() {

    operator fun invoke(parsed: ParsedSmartTitle): RecordTypeHint {
        // 1) Αν είναι future date ή έχει reminder-style keywords → REMINDER
        if (parsed.isFutureDate || parsed.isReminderLike) {
            return RecordTypeHint.REMINDER
        }

        // 2) Αν μοιάζει με fuel ή έχουμε fuelType/lt/€/lt → FUEL
        if (parsed.isFuelLike ||
            parsed.detectedFuelType != null ||
            (parsed.detectedLiters != null && parsed.detectedPricePerLiter != null) ||
            (parsed.detectedLiters != null && parsed.detectedCostEuro != null) ||
            (parsed.detectedCostEuro != null && parsed.detectedPricePerLiter != null)
        ) {
            return RecordTypeHint.FUEL
        }

        // 3) Αν μοιάζει με expense ή έχει κάποια categoryHint → EXPENSE
        if (parsed.isExpenseLike || parsed.categoryHint != null) {
            return RecordTypeHint.EXPENSE
        }

        // 4) Δεν είμαστε σίγουροι → UNKNOWN
        return RecordTypeHint.UNKNOWN
    }
}
