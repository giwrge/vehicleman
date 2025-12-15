package com.vehicleman.domain.use_case.record_ai

import javax.inject.Inject
import kotlin.math.round

/**
 * Use case για αυτόματο υπολογισμό των πεδίων καυσίμων.
 *
 * Λογική:
 * - Αν έχεις cost & liters → pricePerLiter = cost / liters
 * - Αν έχεις cost & pricePerLiter → liters = cost / pricePerLiter
 * - Αν έχεις liters & pricePerLiter → cost = liters * pricePerLiter
 *
 * Όλα στρογγυλοποιούνται σε 2 δεκαδικά (τύπου 29.09).
 */
class AutoFillFuelDataUseCase @Inject constructor() {

    operator fun invoke(request: FuelAutofillRequest): FuelAutofillResult {
        val known = listOf(
            request.costEuro != null,
            request.liters != null,
            request.pricePerLiter != null
        ).count { it }

        if (known != 2) {
            // Δεν υπολογίζουμε τίποτα αν τα γνωστά δεν είναι ΑΚΡΙΒΩΣ δύο.
            return FuelAutofillResult(
                costEuro = request.costEuro,
                liters = request.liters,
                pricePerLiter = request.pricePerLiter,
                fuelTypeHint = request.fuelTypeHint
            )
        }

        var cost = request.costEuro
        var liters = request.liters
        var price = request.pricePerLiter

        if (cost != null && liters != null && price == null) {
            // cost & liters → price
            if (liters > 0.0) {
                price = roundTo2(cost / liters)
            }
        } else if (cost != null && price != null && liters == null) {
            // cost & price → liters
            if (price > 0.0) {
                liters = roundTo2(cost / price)
            }
        } else if (liters != null && price != null && cost == null) {
            // liters & price → cost
            cost = roundTo2(liters * price)
        }

        return FuelAutofillResult(
            costEuro = cost,
            liters = liters,
            pricePerLiter = price,
            fuelTypeHint = request.fuelTypeHint
        )
    }

    private fun roundTo2(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}
