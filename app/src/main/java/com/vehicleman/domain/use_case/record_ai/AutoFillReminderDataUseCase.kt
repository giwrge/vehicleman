package com.vehicleman.domain.use_case.record_ai

import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case για αυτόματο συμπλήρωμα υπενθύμισης.
 * - reminderDate: fallbackDate (ή μπορείς να το override από UI όταν ο χρήστης διαλέξει ημερομηνία)
 * - reminderKm: default τα lastOdometerKm
 * - costEuro: παίρνει generic amount (π.χ. "45e") για να γεμίζει costReminder
 * - autoDescription: τίτλος + κόστος
 */
class AutoFillReminderDataUseCase @Inject constructor() {

    operator fun invoke(
        parsed: ParsedSmartTitle,
        request: ReminderAutofillRequest
    ): ReminderAutofillResult {

        val cost = parsed.detectedGenericAmountEuro ?: parsed.detectedCostEuro
        val reminderDate = parsed.detectedDate ?: request.fallbackDate
        val reminderKm = request.lastOdometerKm

        val description = buildString {
            append(parsed.cleanedText.ifBlank { parsed.raw })
            if (cost != null) append(" — Κόστος: ${formatEuro(cost)}")
        }.trim()

        return ReminderAutofillResult(
            reminderDate = reminderDate,
            reminderKm = reminderKm,
            costEuro = cost,
            autoDescription = description
        )
    }

    private fun formatEuro(value: Double): String = String.format("%.2f €", value)
}

/**
 * Request για reminder autofill.
 */
data class ReminderAutofillRequest(
    val lastOdometerKm: Int?,
    val fallbackDate: LocalDate
)

/**
 * Result για reminder autofill.
 * costEuro -> θα γεμίζει στο UI το costReminder.
 */
data class ReminderAutofillResult(
    val reminderDate: LocalDate?,
    val reminderKm: Int?,
    val costEuro: Double?,
    val autoDescription: String
)
