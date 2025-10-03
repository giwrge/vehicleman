package com.vehicleman.ui.forms

/**
 * State class that holds the data for the Add/Edit Vehicle form.
 * @param vehicleId The ID of the vehicle being edited, or null for a new vehicle.
 */
data class VehicleFormState(
    val vehicleId: String? = null,
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val year: String = "",
    val fuelType: String = "",
    val initialOdometer: String = "", // Keep as String for input fields
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val validationErrors: VehicleFormErrorState = VehicleFormErrorState()
)

/**
 * Data class to hold all potential validation errors for the form fields.
 * If a string is not null, it means there is an error message to display.
 */
data class VehicleFormErrorState(
    val nameError: String? = null,
    val makeError: String? = null,
    val modelError: String? = null,
    val licensePlateError: String? = null,
    val yearError: String? = null,
    val fuelTypeError: String? = null,
    val initialOdometerError: String? = null,
    val generalError: String? = null // For saving failures, etc.
)

/**
 * Sealed class for events that occur on the Add/Edit Vehicle Screen.
 */
sealed class VehicleFormEvent {
    data class NameChanged(val name: String) : VehicleFormEvent()
    data class MakeChanged(val make: String) : VehicleFormEvent()
    data class ModelChanged(val model: String) : VehicleFormEvent()
    data class LicensePlateChanged(val licensePlate: String) : VehicleFormEvent()
    data class YearChanged(val year: String) : VehicleFormEvent()
    data class FuelTypeChanged(val fuelType: String) : VehicleFormEvent()
    data class InitialOdometerChanged(val odometer: String) : VehicleFormEvent()

    object SaveVehicle : VehicleFormEvent()
    object DismissGeneralError : VehicleFormEvent()
}
