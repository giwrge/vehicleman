package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle

/**
 * UI state για το Add/Edit Vehicle form.
 * Τα πεδία εδώ αντιστοιχούν στα πεδία της φόρμας (όλα strings όπου ο χρήστης πληκτρολογεί).
 */
// presentation/vehicles/VehicleFormState.kt

data class VehicleFormState(
    val id: String? = null, // ΠΡΟΣΘΗΚΗ: Για να ξέρουμε αν επεξεργαζόμαστε υπάρχον όχημα
    val make: String = "",
    val model: String = "",
    val fuelType: String = "",
    val plateNumber: String = "",
    val year: String = "",
    val currentOdometer: String = "",
    val oilChangeKm: Long? = null,
    val oilChangeDate: Long? = null,
    val tiresChangeKm: Long? = null,
    val tiresChangeDate: Long? = null,
    val insuranceExpiryDate: Long? = null,
    val taxesExpiryDate: Long? = null,
    val isFormValid: Boolean = false,
    val errorMessage: String? = null
) {
    /** Ενημέρωση πεδίου με όνομα (generic helper). */
    fun copyField(field: String, value: String): VehicleFormState {
        return when (field) {
            "brand" -> copy(make = value) // Το "brand" χρησιμοποιείται ακόμα από παλιά UI components
            "make" -> copy(make = value)
            "model" -> copy(model = value)
            "plateNumber" -> copy(plateNumber = value)
            "year" -> copy(year = value)
            "currentOdometer" -> copy(currentOdometer = value)
            "oilChangeDate" -> copy(oilChangeDate = value.toLongOrNull())
            "oilChangeKm" -> copy(oilChangeKm = value.toLongOrNull())
            "tiresChangeDate" -> copy(tiresChangeDate = value.toLongOrNull())
            "tiresChangeKm" -> copy(tiresChangeKm = value.toLongOrNull())
            "insuranceExpiryDate" -> copy(insuranceExpiryDate = value.toLongOrNull())
            "taxesExpiryDate" -> copy(taxesExpiryDate = value.toLongOrNull())
            "fuelType" -> copy(fuelType = value)
            else -> this
        }
    }
}