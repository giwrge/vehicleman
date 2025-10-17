package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle

/**
 * UI state για το Add/Edit Vehicle form.
 * Τα πεδία εδώ αντιστοιχούν στα πεδία της φόρμας (όλα strings όπου ο χρήστης πληκτρολογεί).
 */
data class VehicleFormState(
    val brand: String = "",
    val model: String = "",
    val plate: String = "",
    val year: String = "",
    val odometer: String = "",
    val oilChangeTime: String = "",
    val oilChangeKm: String = "",
    val tiresChangeTime: String = "",
    val tiresChangeKm: String = "",
    val insuranceDate: String = "",
    val taxDate: String = "",
    val fuelType: String = "",
    val currentVehicle: Vehicle? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null,
    val limitReached: Boolean = false
) {
    /** Ενημέρωση πεδίου με όνομα (generic helper). */
    fun copyField(field: String, value: String): VehicleFormState {
        return when (field) {
            "brand" -> copy(brand = value)
            "model" -> copy(model = value)
            "plate" -> copy(plate = value)
            "year" -> copy(year = value)
            "odometer" -> copy(odometer = value)
            "oilChangeTime" -> copy(oilChangeTime = value)
            "oilChangeKm" -> copy(oilChangeKm = value)
            "tiresChangeTime" -> copy(tiresChangeTime = value)
            "tiresChangeKm" -> copy(tiresChangeKm = value)
            "insuranceDate" -> copy(insuranceDate = value)
            "taxDate" -> copy(taxDate = value)
            "fuelType" -> copy(fuelType = value)
            else -> this
        }
    }
}
