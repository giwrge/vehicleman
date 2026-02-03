package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject

class PredictRecordTypeFromParsedDataUseCase @Inject constructor() {

    operator fun invoke(parsed: ParsedSmartTitle): RecordTypeHint {
        return when {
            // The flags are now accurately calculated in ParseSmartTitleUseCase
            // based on the powerful RecordCategorizer's result.
            parsed.isReminderLike -> RecordTypeHint.REMINDER
            parsed.isFuelLike -> RecordTypeHint.FUEL
            parsed.isExpenseLike -> RecordTypeHint.EXPENSE
            else -> RecordTypeHint.UNKNOWN
        }
    }
}
