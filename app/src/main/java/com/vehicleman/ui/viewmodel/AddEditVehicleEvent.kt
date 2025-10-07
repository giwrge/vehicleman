package com.vehicleman.ui.viewmodel

/** Sealed Class για τα συμβάντα (Events). */
sealed class AddEditVehicleEvent {
    data class NameChanged(val name: String) : AddEditVehicleEvent()
    data class MakeChanged(val make: String) : AddEditVehicleEvent()
    data class ModelChanged(val model: String) : AddEditVehicleEvent()
    data class YearChanged(val year: String) : AddEditVehicleEvent()
    data class LicensePlateChanged(val licensePlate: String) : AddEditVehicleEvent()
    data class FuelTypeSelected(val fuelType: String) : AddEditVehicleEvent()
    data class InitialOdometerChanged(val odometer: String) : AddEditVehicleEvent()
    object SaveVehicle : AddEditVehicleEvent()
}
