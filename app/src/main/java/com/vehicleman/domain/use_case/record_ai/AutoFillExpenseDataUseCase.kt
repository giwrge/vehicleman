package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject

/**
 * Αυτό το use case δημιουργεί έξυπνη περιγραφή και αρχικοποίηση πεδίων
 * για EXPENSE-type εγγραφές (service, λάστιχα, πλύσιμο, ασφάλεια κτλ.)
 * Δεν γνωρίζει τίποτα για RecordEntity/Domain – επιστρέφει καθαρά δεδομένα.
 */
class AutoFillExpenseDataUseCase @Inject constructor() {

    operator fun invoke(parsed: ParsedSmartTitle): ExpenseAutofillResult {
        val items = parsed.detectedServiceItems
        val category = parsed.categoryHint
        val cost = parsed.detectedGenericAmountEuro

        val description = buildDescription(
            category = category,
            items = items,
            cost = cost
        )

        return ExpenseAutofillResult(
            categoryHint = category,
            items = items,
            costEuro = cost,
            autoDescription = description
        )
    }

    private fun buildDescription(
        category: CategoryHint?,
        items: List<String>,
        cost: Double?
    ): String {
        val builder = StringBuilder()

        if (category != null) {
            builder.append(categoryToHumanTitle(category))
        }

        // expand items
        if (items.isNotEmpty()) {
            if (builder.isNotEmpty()) builder.append(": ")
            builder.append(items.joinToString(", "))
        }

        if (cost != null) {
            if (builder.isNotEmpty()) builder.append(" – ")
            builder.append("Κόστος: ${formatEuro(cost)}")
        }

        return builder.toString().trim()
    }

    private fun categoryToHumanTitle(category: CategoryHint): String {
        return when (category) {
            CategoryHint.SERVICE -> "Service"
            CategoryHint.TIRES -> "Ελαστικά"
            CategoryHint.INSURANCE -> "Ασφάλεια"
            CategoryHint.TAXES -> "Τέλη κυκλοφορίας"
            CategoryHint.WASH -> "Πλύσιμο"
            else -> "Έξοδο"
        }
    }

    private fun formatEuro(value: Double): String {
        return String.format("%.2f €", value)
    }
}

/**
 * Αποτέλεσμα για EXPENSE.
 * Μπορεί να μπει κατευθείαν στο AddEditRecordState.
 */
data class ExpenseAutofillResult(
    val categoryHint: CategoryHint?,
    val items: List<String>,
    val costEuro: Double?,
    val autoDescription: String
)
