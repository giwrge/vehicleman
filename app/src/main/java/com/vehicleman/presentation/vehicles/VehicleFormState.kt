package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle

/**
 * UI state για το Add/Edit Vehicle form.
 * Τα πεδία εδώ αντιστοιχούν στα πεδία της φόρμας (όλα strings όπου ο χρήστης πληκτρολογεί).
 */
// presentation/vehicles/VehicleFormState.kt

data class VehicleFormState(
    // ΠΡΙΝ: val brand: String = "",
    val make: String = "", // ΔΙΟΡΘΩΣΗ
    val model: String = "",
    val fuelType: String = "",
    val plateNumber: String = "", // ΔΙΟΡΘΩΣΗ
    val year: String = "",
    val currentOdometer: String = "",
    val oilChangeKm: Long? = null,
    val oilChangeDate: Long? = null,
    val tiresChangeKm: Long? = null,
    val tiresChangeDate: Long? = null,
    val insuranceExpiryDate: Long? = null,
    val taxesExpiryDate: Long? = null, // ΔΙΟΡΘΩΣΗ
    val isFormValid: Boolean = false,
    val errorMessage: String? = null

) {
    /** Ενημέρωση πεδίου με όνομα (generic helper). */
    fun copyField(field: String, value: String): VehicleFormState {
        return when (field) {
            "brand" -> copy(make = value)
            "model" -> copy(model = value)
            "plate" -> copy(plateNumber = value)
            "year" -> copy(year = value)
            "odometer" -> copy(currentOdometer = value)
            "oilChangeTime" -> copy(oilChangeDate = value.toLongOrNull())
            "oilChangeKm" -> copy(oilChangeKm = value.toLongOrNull())
            "tiresChangeTime" -> copy(tiresChangeDate = value.toLongOrNull())
            "tiresChangeKm" -> copy(tiresChangeKm = value.toLongOrNull())
            "insuranceDate" -> copy(insuranceExpiryDate = value.toLongOrNull())
            "taxDate" -> copy(taxesExpiryDate = value.toLongOrNull())
            "fuelType" -> copy(fuelType = value)
            else -> this
        }
    }
}