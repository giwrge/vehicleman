// app/src/main/java/com/vehicleman/presentation/vehicles/VehicleFormState.kt
package com.vehicleman.presentation.vehicles

import java.util.Date

data class VehicleFormState(
    val vehicleId: String = "",
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = "",
    val fuelType: String = "",
    val initialOdometer: String = "",
    val registrationDate: Date = Date(),

    // ΝΕΑ ΠΕΔΙΑ: Διαστήματα Συντήρησης
    val oilChangeIntervalKm: String = "10000",
    val oilChangeIntervalDays: String = "365",

    val nameError: String? = null,
    val makeError: String? = null,
    val modelError: String? = null,
    val yearError: String? = null,
    val initialOdometerError: String? = null,

    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)