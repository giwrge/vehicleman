package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import java.util.Date
import javax.inject.Inject

/**
 * Το απόλυτο “AI brain” της AddEditRecordScreen.
 *
 * Αναλύει τον τίτλο, αποφασίζει τι τύπος εγγραφής είναι,
 * συμπληρώνει αυτόματα πεδία, παράγει περιγραφή,
 * και καθορίζει ποια φόρμα πρέπει να εμφανιστεί.
 */
class SmartRecordBuilderUseCase @Inject constructor(
    private val parseUseCase: ParseRecordTitleUseCase,
    private val categorizerUseCase: RecordCategorizerUseCase,
    private val autoFuel: AutoFillFuelDataUseCase
) {

    data class SmartRecordResult(
        val detectedType: RecordType,
        val category: RecordCategory?,
        val autoTitle: String?,
        val autoDescription: String?,
        val autoCost: String?,
        val autoQuantity: String?,
        val autoPricePerUnit: String?,
        val autoFuelType: String?,
        val autoOdometer: String?,
        val autoReminderDate: Date?,
        val autoReminderOdometer: String?,
        val showFuelFields: Boolean,
        val showReminderFields: Boolean,
        val showExpenseFields: Boolean
    )

    operator fun invoke(
        title: String,
        description: String?,
        chosenDate: Date,
        lastOdometer: Int?,
        previousFuelOdo: Int?
    ): SmartRecordResult {

        // ---------------------------------------------------------------------
        // 1) Parse raw title → detect numbers, cost, lt, € etc.
        // ---------------------------------------------------------------------
        val parsed = parseUseCase(title)

        // ---------------------------------------------------------------------
        // 2) Categorize → Fuel / Service / Tires / Reminder / Unknown
        // ---------------------------------------------------------------------
        val category = categorizerUseCase(
            title = title,
            isReminder = false,
            description = description
        )

        val isLikelyFuel = parsed.isFuelLike
                || category is RecordCategory.ExpenseCategory.Fuel

        // Reminder detection: αν η ημερομηνία είναι μελλοντική
        val isFutureDate = chosenDate.after(Date())
        val isLikelyReminder = isFutureDate && !isLikelyFuel

        val detectedType = when {
            isLikelyFuel -> RecordType.FUEL_UP
            isLikelyReminder -> RecordType.REMINDER
            else -> RecordType.EXPENSE
        }

        // ---------------------------------------------------------------------
        // 3) Fuel auto-fill logic
        // ---------------------------------------------------------------------
        var autoCost: String? = null
        var autoQty: String? = null
        var autoPPU: String? = null
        var autoFuelType: String? = null
        var autoDesc: String? = null

        if (detectedType == RecordType.FUEL_UP) {
            val fuel = autoFuel(
                input = parsed,
                lastOdometer = lastOdometer,
                previousFuelOdometer = previousFuelOdo
            )

            autoCost = fuel.cost?.toString()
            autoQty = fuel.liters?.toString()
            autoPPU = fuel.pricePerLiter?.toString()
            autoFuelType = fuel.fuelType
            autoDesc = fuel.description
        }

        // ---------------------------------------------------------------------
        // 4) Reminder auto-fill
        // ---------------------------------------------------------------------
        val autoReminderDate: Date? =
            if (detectedType == RecordType.REMINDER) chosenDate else null

        val autoReminderOdo: String? =
            if (detectedType == RecordType.REMINDER)
                lastOdometer?.toString()
            else null

        // ---------------------------------------------------------------------
        // 5) UI visibility rules
        // ---------------------------------------------------------------------
        val showFuelFields = detectedType == RecordType.FUEL_UP
        val showReminderFields = detectedType == RecordType.REMINDER
        val showExpenseFields =
            detectedType == RecordType.EXPENSE || detectedType == RecordType.FUEL_UP

        // ---------------------------------------------------------------------
        // 6) Return final AI-powered result
        // ---------------------------------------------------------------------
        return SmartRecordResult(
            detectedType = detectedType,
            category = category,
            autoTitle = title,
            autoDescription = autoDesc,
            autoCost = autoCost,
            autoQuantity = autoQty,
            autoPricePerUnit = autoPPU,
            autoFuelType = autoFuelType,
            autoOdometer = lastOdometer?.toString(),
            autoReminderDate = autoReminderDate,
            autoReminderOdometer = autoReminderOdo,
            showFuelFields = showFuelFields,
            showReminderFields = showReminderFields,
            showExpenseFields = showExpenseFields
        )
    }
}
