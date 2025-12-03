package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Υπολογίζει έξυπνα τα πεδία καυσίμων:
 *
 * - ποσότητα lt
 * - κόστος €
 * - τιμή/lt
 * - περιγραφή
 * - KM από προηγούμενο refueling (αν δοθεί)
 */
class AutoFillFuelDataUseCase @Inject constructor() {

    data class FuelResult(
        val liters: Double?,
        val cost: Double?,
        val pricePerLiter: Double?,
        val fuelType: String?,
        val description: String?
    )

    operator fun invoke(
        input: ParsedInput,
        lastOdometer: Int?,
        previousFuelOdometer: Int? // τελευταίο fuel_up πριν από αυτό
    ): FuelResult {

        var liters = input.detectedLiters
        var cost = input.detectedEuroCost
        var ppu = input.detectedPricePerLiter
        var type = input.detectedFuelType

        // -------------------------------------------------------------
        // 1) Fuel type guess (αν δεν βρέθηκε από parsing)
        // -------------------------------------------------------------
        if (type == null) {
            type = guessFuelType(input.normalized)
        }

        // -------------------------------------------------------------
        // 2) Compute missing fields
        // -------------------------------------------------------------

        // Case A: cost + ppu → compute liters
        if (liters == null && cost != null && ppu != null && ppu > 0) {
            liters = (cost / ppu)
        }

        // Case B: liters + ppu → compute cost
        if (cost == null && liters != null && ppu != null) {
            cost = liters * ppu
        }

        // Case C: liters + cost → compute ppu
        if (ppu == null && liters != null && cost != null && liters > 0) {
            ppu = cost / liters
        }

        // Round for UI neatness
        liters = liters?.let { (it * 100).roundToInt() / 100.0 }
        cost = cost?.let { (it * 100).roundToInt() / 100.0 }
        ppu = ppu?.let { (it * 1000).roundToInt() / 1000.0 }

        // -------------------------------------------------------------
        // 3) Compute distance since last refuel (description)
        // -------------------------------------------------------------
        val rangeDescription = if (lastOdometer != null && previousFuelOdometer != null) {
            val diff = lastOdometer - previousFuelOdometer
            if (diff > 0) " — ${diff} km από το προηγούμενο γέμισμα" else ""
        } else {
            ""
        }

        val description = if (liters != null && ppu != null) {
            "Ανεφοδιασμός: $liters lt @ $ppu €/lt$rangeDescription"
        } else null

        return FuelResult(
            liters = liters,
            cost = cost,
            pricePerLiter = ppu,
            fuelType = type,
            description = description
        )
    }

    // ---------------------------------------------------------------------
    // Guess fuel type from text
    // ---------------------------------------------------------------------
    private fun guessFuelType(text: String): String? {
        return when {
            "100" in text && ("ben" in text || "unleaded" in text) -> "unleaded_100"
            "98" in text -> "unleaded_98"
            "95" in text -> "unleaded_95"
            "diesel" in text || "dizel" in text -> "diesel"
            "lpg" in text || "ygraerio" in text -> "lpg"
            "cng" in text -> "cng"
            else -> null
        }
    }
}
