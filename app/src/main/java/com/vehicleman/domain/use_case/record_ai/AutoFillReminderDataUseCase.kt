package com.vehicleman.domain.use_case.record_ai

import java.time.LocalDate
import javax.inject.Inject

/**
 * Συμπληρώνει αυτόματα πεδία υπενθύμισης:
 *
 * - ημερομηνία
 * - km υπενθύμισης
 * - περιγραφή
 *
 * Βασίζεται στα στοιχεία του parser + στο "τελευταίο οδόμετρο" του οχήματος.
 */
class AutoFillReminderDataUseCase @Inject constructor() {

    operator fun invoke(
        parsed: ParsedSmartTitle,
        request: ReminderAutofillRequest
    ): ReminderAutofillResult {

        val reminderDate = parsed.detectedDate ?: request.fallbackDate
        val reminderKm = request.lastOdometerKm

        val reason = detectReminderType(parsed)

        val description = buildDescription(
            reason = reason,
            date = reminderDate,
            km = reminderKm,
            cost = parsed.detectedGenericAmountEuro
        )

        return ReminderAutofillResult(
            reminderDate = reminderDate,
            reminderKm = reminderKm,
            reminderReason = reason,
            autoDescription = description
        )
    }

    /**
     * Προσπαθούμε να εντοπίσουμε τον τύπο υπενθύμισης.
     */
    private fun detectReminderType(parsed: ParsedSmartTitle): ReminderReason {
        val text = parsed.normalized.lowercase()

        return when {
            text.contains("kteo") || text.contains("κτεο") -> ReminderReason.KTEO
            text.contains("asfal") || text.contains("ασφαλ") -> ReminderReason.INSURANCE
            text.contains("teli") || text.contains("τελη") -> ReminderReason.TAXES
            text.contains("service") || text.contains("σερβις") -> ReminderReason.SERVICE
            else -> ReminderReason.GENERIC
        }
    }

    private fun buildDescription(
        reason: ReminderReason,
        date: LocalDate,
        km: Int?,
        cost: Double?
    ): String {
        val builder = StringBuilder()

        builder.append("Υπενθύμιση ")
        builder.append(reasonToText(reason))
        builder.append(" για ")
        builder.append(dateToText(date))

        if (km != null) {
            builder.append(" – ")
            builder.append("$km km")
        }

        if (cost != null) {
            builder.append(" – Κόστος: ${String.format("%.2f €", cost)}")
        }

        return builder.toString()
    }

    private fun reasonToText(reason: ReminderReason): String {
        return when (reason) {
            ReminderReason.KTEO -> "ΚΤΕΟ"
            ReminderReason.INSURANCE -> "Ασφάλεια"
            ReminderReason.TAXES -> "Τέλη"
            ReminderReason.SERVICE -> "Service"
            ReminderReason.GENERIC -> "εργασία"
        }
    }

    private fun dateToText(date: LocalDate): String {
        return "${date.dayOfMonth}/${date.monthValue}/${date.year}"
    }
}

/**
 * Τύποι υπενθύμισης.
 */
enum class ReminderReason {
    KTEO,
    INSURANCE,
    TAXES,
    SERVICE,
    GENERIC
}

/**
 * Τι χρειαζόμαστε για τις υπενθυμίσεις.
 */
data class ReminderAutofillRequest(
    val lastOdometerKm: Int?,
    val fallbackDate: LocalDate = LocalDate.now()
)

/**
 * Αποτέλεσμα για το UI.
 */
data class ReminderAutofillResult(
    val reminderDate: LocalDate?,
    val reminderKm: Int?,
    val reminderReason: ReminderReason,
    val autoDescription: String
)
