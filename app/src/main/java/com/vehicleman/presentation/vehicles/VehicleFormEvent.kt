package com.vehicleman.presentation.vehicles

/**
 * Εκδηλώσεις (events) για τη φόρμα οχήματος.
 * - Αντιπροσωπεύουν ενέργειες του χρήστη (π.χ. αλλαγή πεδίου, αποθήκευση, φόρτωση).
 */
sealed class VehicleFormEvent {
    data class DeleteVehicle(val vehicleId: String): VehicleFormEvent()
    data class LoadVehicle(val vehicleId: String): VehicleFormEvent()

    data class MakeChanged(val make: String) : VehicleFormEvent()
    data class ModelChanged(val model: String) : VehicleFormEvent()
    data class PlateNumberChanged(val plateNumber: String) : VehicleFormEvent()
    data class YearChanged(val year: String) : VehicleFormEvent()
    data class FuelTypeChanged(val fuelTypes: String) : VehicleFormEvent()
    data class CurrentOdometerChanged(val currentOdometer: String) : VehicleFormEvent()
    data class OilChangeKmChanged(val oilChangeKm: String) : VehicleFormEvent()
    data class OilChangeDateChanged(val oilChangeDate: String) : VehicleFormEvent()
    data class TiresChangeDateChanged(val tiresChangeDate: String) : VehicleFormEvent()
    data class TiresChangeKmChanged(val tiresChangeKm: String) : VehicleFormEvent()
    data class InsuranceExpiryDateChanged(val insuranceExpiryDate: String) : VehicleFormEvent()
    data class TaxesExpiryDateChanged(val taxesExpiryDate: String) : VehicleFormEvent()
    object Submit : VehicleFormEvent()
}
