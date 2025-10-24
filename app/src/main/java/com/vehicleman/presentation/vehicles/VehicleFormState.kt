package com.vehicleman.presentation.vehicles

/**
 * UI state για το Add/Edit Vehicle form.
 * Τα πεδία εδώ αντιστοιχούν στα πεδία της φόρμας (όλα strings όπου ο χρήστης πληκτρολογεί).
 */
data class VehicleFormState(
    val id: String? = null,
    val make: String = "",
    val model: String = "",
    val fuelType: String = "",
    val plateNumber: String = "",
    val year: String = "",
    val currentOdometer: String = "",
    val oilChangeKm: String = "",
    val oilChangeDate: String = "",
    val tiresChangeKm: String = "",
    val tiresChangeDate: String = "",
    val insuranceExpiryDate: String = "",
    val taxesExpiryDate: String = "",
    val isFormValid: Boolean = false,
    val errorMessage: String? = null
)
