package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle
import java.util.Date
import java.util.UUID

/** Μετατροπές μεταξύ UI state και Domain model **/

// Maps the UI-friendly VehicleFormState to the strict, data-layer Vehicle model
fun VehicleFormState.toVehicle(): Vehicle {
    return Vehicle(
        id = this.id ?: UUID.randomUUID().toString(),
        name = "${this.make} ${this.model}".trim(),
        make = this.make,
        model = this.model,
        plateNumber = this.plateNumber,
        year = this.year.toIntOrNull() ?: 0,
        fuelType = this.fuelType,
        currentOdometer = this.currentOdometer.toIntOrNull() ?: 0,
        registrationDate = Date(), // New vehicles get current date, existing ones should preserve it (handled in repository)
        oilChangeKm = this.oilChangeKm.toLongOrNull() ?: 10000L,
        oilChangeDate = this.oilChangeDate.toLongOrNull() ?: 365L,
        tiresChangeKm = this.tiresChangeKm.toLongOrNull() ?: 40000L,
        tiresChangeDate = this.tiresChangeDate.toLongOrNull() ?: (365L * 2),
        insuranceExpiryDate = this.insuranceExpiryDate.toLongOrNull() ?: 365L,
        taxesExpiryDate = this.taxesExpiryDate.toLongOrNull() ?: 365L
    )
}

// Maps the data-layer Vehicle model to the UI-friendly VehicleFormState
fun Vehicle.toFormState(): VehicleFormState {
    return VehicleFormState(
        id = this.id,
        make = this.make,
        model = this.model,
        plateNumber = this.plateNumber,
        year = this.year.toString(),
        currentOdometer = this.currentOdometer.toString(),
        fuelType = this.fuelType,
        oilChangeKm = this.oilChangeKm.toString(),
        oilChangeDate = this.oilChangeDate.toString(),
        tiresChangeKm = this.tiresChangeKm.toString(),
        tiresChangeDate = this.tiresChangeDate.toString(),
        insuranceExpiryDate = this.insuranceExpiryDate.toString(),
        taxesExpiryDate = this.taxesExpiryDate.toString(),
        isFormValid = false, // Reset form validity
        errorMessage = null // Clear any previous errors
    )
}
