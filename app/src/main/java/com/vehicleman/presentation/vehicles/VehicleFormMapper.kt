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
        oilChangeKm = this.oilChangeKm ?: 10000L,
        oilChangeDate = this.oilChangeDate ?: 365L,
        tiresChangeKm = this.tiresChangeKm ?: 40000L,
        tiresChangeDate = this.tiresChangeDate ?: (365L * 2),
        insuranceExpiryDate = this.insuranceExpiryDate ?: 365L,
        taxesExpiryDate = this.taxesExpiryDate ?: 365L
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
        oilChangeKm = this.oilChangeKm,
        oilChangeDate = this.oilChangeDate,
        tiresChangeKm = this.tiresChangeKm,
        tiresChangeDate = this.tiresChangeDate,
        insuranceExpiryDate = this.insuranceExpiryDate,
        taxesExpiryDate = this.taxesExpiryDate,
        isFormValid = false, // Reset form validity
        errorMessage = null // Clear any previous errors
    )
}
