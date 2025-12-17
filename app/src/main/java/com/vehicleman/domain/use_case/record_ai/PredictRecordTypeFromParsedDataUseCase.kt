package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject

class PredictRecordTypeFromParsedDataUseCase @Inject constructor() {

    operator fun invoke(parsed: ParsedSmartTitle): RecordTypeHint {

        // 1) future date ή reminder signals
        if (parsed.isFutureDate || parsed.isReminderLike) return RecordTypeHint.REMINDER

        // 2) fuel signals
        if (parsed.isFuelLike ||
            parsed.detectedFuelType != null ||
            (parsed.detectedLiters != null && parsed.detectedPricePerLiter != null) ||
            (parsed.detectedLiters != null && parsed.detectedCostEuro != null) ||
            (parsed.detectedCostEuro != null && parsed.detectedPricePerLiter != null)
        ) return RecordTypeHint.FUEL

        // 3) expense signals
        if (parsed.isExpenseLike || parsed.categoryHint != null) return RecordTypeHint.EXPENSE

        return RecordTypeHint.UNKNOWN
    }
}
