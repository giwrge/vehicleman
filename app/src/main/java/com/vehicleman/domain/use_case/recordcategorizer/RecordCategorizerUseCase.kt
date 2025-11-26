package com.vehicleman.domain.use_case.recordcategorizer

import com.vehicleman.domain.model.category.RecordCategory
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

class RecordCategorizerUseCase @Inject constructor() {

    /**
     * @param title        Ο τίτλος της εγγραφής (υποχρεωτικός λογικά)
     * @param isReminder   true αν η εγγραφή είναι υπενθύμιση
     * @param description  προαιρετική περιγραφή (αν υπάρχει)
     */
    operator fun invoke(
        title: String,
        isReminder: Boolean,
        description: String? = null
    ): RecordCategory {

        // 1) ΠΡΩΤΑ: Synonym Engine (English + Greek + Greeklish)
        val synonymCategory = RecordSynonymNormalizer.detectCategory(
            title = title,
            description = description,
            isReminder = isReminder
        )
        if (synonymCategory != null) {
            return synonymCategory
        }

        // 2) ΔΕΥΤΕΡΟ: Μερικοί επιπλέον "χειροποίητοι" κανόνες (αν θες)
        val normalizedTitle = normalizeString(title)

        // Παράδειγμα: αν θες να κρατήσεις κάποια pure Greek patterns:
        when {
            "αγορα καυσιμων" in normalizedTitle ||
                    "καυσιμα" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Fuel.FuelPurchase
            }

            "αλλαγη λαδιων" in normalizedTitle ||
                    "λαδια" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Service.OilChange
            }

            "ζυγοσταθμιση" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Tires.WheelBalancing
            }

            "ευθυγραμμιση" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Tires.WheelAlignment
            }

            "φρενα" in normalizedTitle ||
                    "τακακια" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Repairs.Brakes
            }

            "τελη κυκλοφοριας" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Legal.RoadTax
            }

            "ασφαλεια" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Legal.Insurance
            }

            "κτεο" in normalizedTitle || "mot" in normalizedTitle -> {
                return RecordCategory.ExpenseCategory.Legal.KteoMot
            }
        }

        // 3) ΤΕΛΙΚΟ fallback
        return if (isReminder) {
            RecordCategory.ReminderCategory.GeneralReminder
                .takeIf { title.isNotBlank() }
                ?: RecordCategory.UnknownReminder
        } else {
            RecordCategory.UnknownExpense
        }
    }

    private fun normalizeString(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase(Locale.getDefault())
    }
}

