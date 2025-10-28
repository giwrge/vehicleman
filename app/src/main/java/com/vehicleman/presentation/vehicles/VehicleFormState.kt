package com.vehicleman.presentation.vehicles

import java.util.Date

/**
 * UI state for the Add/Edit Vehicle form.
 */
data class VehicleFormState(
    val id: String? = null,
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val fuelTypes: String = "", // Keep as comma-separated string for easy editing
    val plateNumber: String = "",
    val year: String = "",
    val currentOdometer: String = "",
    val registrationDate: Date = Date(),
    val oilChangeKm: String = "",
    val oilChangeDate: String = "",
    val tiresChangeKm: String = "",
    val tiresChangeDate: String = "",
    val insuranceExpiryDate: String = "",
    val taxesExpiryDate: String = "",
    val isFormValid: Boolean = false,
    val errorMessage: String? = null
)
