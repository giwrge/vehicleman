package com.vehicleman.presentation.vehicles

/**
 * State class that holds the data for the Add/Edit Vehicle form.
 */
data class VehicleFormState(
    val vehicleId: String? = null, // Το ID του οχήματος (null για νέο).
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val year: String = "",
    val fuelType: String = "Βενζίνη", // Προκαθορισμένη τιμή όπως στο UI
    val initialOdometer: String = "", // String για τα πεδία εισόδου
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false, // Προσθήκη για διαχείριση mode
    val showPaywall: Boolean = false, // Προσθήκη για έλεγχο ορίου
    val validationErrors: VehicleFormErrorState = VehicleFormErrorState()
)

/**
 * Data class to hold all potential validation errors for the form fields.
 */
data class VehicleFormErrorState(
    val nameError: String? = null,
    val makeError: String? = null,
    val modelError: String? = null,
    val licensePlateError: String? = null,
    val yearError: String? = null,
    val fuelTypeError: String? = null,
    val initialOdometerError: String? = null,
    val generalError: String? = null // Για αποτυχίες αποθήκευσης, κλπ.
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
