package com.vehicleman.ui.viewmodel

/** Κλάση κατάστασης για τη φόρμα. */
data class AddEditVehicleState(
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = "",
    val fuelType: String = "Βενζίνη",
    val initialOdometer: String = "",
    val isEditMode: Boolean = false,
    val isSavedSuccessfully: Boolean = false,
    val showPaywall: Boolean = false,
    val error: String? = null,
    val isReady: Boolean = false // Χρησιμοποιείται όταν φορτώνουμε για επεξεργασία
)
