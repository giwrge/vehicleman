package com.vehicleman.domain.use_case.record_ai

/**
 * Το αποτέλεσμα του parsing:
 * - αναγνωρισμένοι αριθμοί
 * - ποσά
 * - lt
 * - €/lt
 * - cost
 * - πιθανές ενδείξεις ότι είναι fuel
 */
data class ParsedInput(
    val rawTitle: String,

    // Numeric detections
    val numbers: List<Double> = emptyList(),

    // Expenses fields
    val cost: Double? = null,

    // Fuel fields
    val liters: Double? = null,
    val pricePerLiter: Double? = null,
    val fuelTypeKeyword: String? = null,

    // Flags
    val isFuelLike: Boolean = false
)
