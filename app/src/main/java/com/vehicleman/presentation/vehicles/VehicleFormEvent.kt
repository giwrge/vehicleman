package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle

/**
 * Εκδηλώσεις (events) για τη φόρμα οχήματος.
 * - Αντιπροσωπεύουν ενέργειες του χρήστη (π.χ. αλλαγή πεδίου, αποθήκευση, φόρτωση).
 */
sealed class VehicleFormEvent {
    data class FieldChanged(val fieldName: String, val value: String): VehicleFormEvent()
    data class SaveVehicle(val vehicle: Vehicle): VehicleFormEvent()
    data class DeleteVehicle(val vehicleId: String): VehicleFormEvent()
    data class LoadVehicle(val vehicleId: String): VehicleFormEvent()
}
