package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle

/**
 * State class that holds the data for the Add/Edit Vehicle form.
 *
 * @param isEditMode True if we are editing an existing vehicle, false for new.
 * @param isSavedSuccess True if the save operation was successful.
 * @param validationErrors Holds specific error messages for form fields.
 */
data class VehicleFormState(
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val year: String = "", // String for input field flexibility
    val fuelType: String = "Βενζίνη",
    val initialOdometer: String = "", // String for input field flexibility
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isReady: Boolean = false, // True when data is loaded for edit, or immediately for new
    val isSavedSuccess: Boolean = false,
    val showPaywall: Boolean = false,
    val validationErrors: VehicleFormErrorState = VehicleFormErrorState()
)

/**
 * Data class to hold all potential validation errors for the form fields.
 */
data class VehicleFormErrorState(
    val nameError: String? = null,
    val initialOdometerError: String? = null,
    val generalError: String? = null // For saving failures, paywall messages, etc.
)