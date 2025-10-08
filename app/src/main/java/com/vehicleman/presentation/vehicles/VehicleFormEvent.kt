package com.vehicleman.presentation.vehicles

/**
 * Sealed class for events that occur on the Add/Edit Vehicle Screen (The Form).
 */
sealed class VehicleFormEvent {
    data class NameChanged(val name: String) : VehicleFormEvent()
    data class MakeChanged(val make: String) : VehicleFormEvent()
    data class ModelChanged(val model: String) : VehicleFormEvent()
    data class LicensePlateChanged(val licensePlate: String) : VehicleFormEvent()
    data class YearChanged(val year: String) : VehicleFormEvent()
    data class FuelTypeSelected(val fuelType: String) : VehicleFormEvent()
    data class InitialOdometerChanged(val odometer: String) : VehicleFormEvent()

    object SaveVehicle : VehicleFormEvent()
}