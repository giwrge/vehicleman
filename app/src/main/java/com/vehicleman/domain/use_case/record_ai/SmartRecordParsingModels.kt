package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.category.RecordCategory
import java.time.LocalDate

/**
 * Κεντρικό αποτέλεσμα parsing του τίτλου.
 *
 * Αυτό ΔΕΝ γνωρίζει τίποτα για Record, Entities κτλ.
 * Είναι καθαρό μοντέλο που μπορείς να περάσεις στο ViewModel / State.
 */
data class ParsedSmartTitle(
    val raw: String,
    val normalized: String,
    val tokens: List<String>,

    // High-level flags
    val isFuelLike: Boolean,
    val isExpenseLike: Boolean,
    val isReminderLike: Boolean,

    // The detected category from RecordCategorizer
    val detectedCategory: RecordCategory,

    // Καύσιμα
    val detectedFuelType: FuelTypeHint?,
    val detectedLiters: Double?,
    val detectedPricePerLiter: Double?,
    val detectedCostEuro: Double?,

    // Γενικά ποσά (π.χ. service 160e)
    val detectedGenericAmountEuro: Double?,

    // Service / expense keywords
    val detectedServiceItems: List<String>,

    // Ημερομηνίες (για υπενθυμίσεις κ.λπ.)
    val detectedDate: LocalDate?,
    val isFutureDate: Boolean
)

/**
 * Τι είδους εγγραφή "μοιάζει" να είναι, χωρίς να δένουμε τον κόμβο
 * με το δικό σου RecordType.
 */
enum class RecordTypeHint {
    FUEL,
    EXPENSE,
    REMINDER,
    UNKNOWN
}

/**
 * Fuel types σε επίπεδο parsing. Δεν είναι ανάγκη να ταιριάζουν 1:1
 * με τα δικά σου enums – μπορείς να τα μεταφράσεις.
 */
enum class FuelTypeHint {
    UNLEADED_95,
    UNLEADED_100,
    DIESEL,
    LPG,
    CNG,
    ELECTRIC,
    UNKNOWN
}

/**
 * Αίτημα για AutoFillFuelDataUseCase:
 * Δίνεις ό,τι έχεις (από UI ή από parser) και υπολογίζει το *ένα* που λείπει.
 * Κανόνας:
 * - Αν είναι γνωστά ΑΚΡΙΒΩΣ δύο από costEuro / liters / pricePerLiter,
 *   τότε το use case υπολογίζει το τρίτο.
 * - Αν είναι γνωστά 0 ή 1 ή και τα 3 → δεν κάνει μαγικά, απλά επιστρέφει όπως είναι.
 */
data class FuelAutofillRequest(
    val costEuro: Double?,
    val liters: Double?,
    val pricePerLiter: Double?,
    val fuelTypeHint: FuelTypeHint?
)

/**
 * Αποτέλεσμα από AutoFillFuelDataUseCase.
 */
data class FuelAutofillResult(
    val costEuro: Double?,
    val liters: Double?,
    val pricePerLiter: Double?,
    val fuelTypeHint: FuelTypeHint?
)
