package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.category.RecordCategory
import javax.inject.Inject

/**
 * Αυτό το use case δημιουργεί έξυπνη περιγραφή και αρχικοποίηση πεδίων
 * για EXPENSE-type εγγραφές (service, λάστιχα, πλύσιμο, ασφάλεια κτλ.)
 * Δεν γνωρίζει τίποτα για RecordEntity/Domain – επιστρέφει καθαρά δεδομένα.
 */
class AutoFillExpenseDataUseCase @Inject constructor() {

    operator fun invoke(parsed: ParsedSmartTitle): ExpenseAutofillResult {
        val items = parsed.detectedServiceItems
        val category = parsed.detectedCategory
        val cost = parsed.detectedGenericAmountEuro

        val description = buildDescription(
            category = category,
            items = items,
            cost = cost
        )

        return ExpenseAutofillResult(
            recordCategory = category,
            items = items,
            costEuro = cost,
            autoDescription = description
        )
    }

    private fun buildDescription(
        category: RecordCategory,
        items: List<String>,
        cost: Double?
    ): String {
        val builder = StringBuilder()

        val title = category.javaClass.simpleName
        builder.append(title)

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

    private fun formatEuro(value: Double): String {
        return String.format("%.2f €", value)
    }
}

/**
 * Αποτέλεσμα για EXPENSE.
 * Μπορεί να μπει κατευθείαν στο AddEditRecordState.
 */
data class ExpenseAutofillResult(
    val recordCategory: RecordCategory,
    val items: List<String>,
    val costEuro: Double?,
    val autoDescription: String
)
