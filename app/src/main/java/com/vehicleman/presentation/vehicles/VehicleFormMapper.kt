package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle


/** Μετατροπές μεταξύ UI state και Domain model **/

// Maps the UI-friendly VehicleFormState to the strict, data-layer Vehicle model

    fun Vehicle.toFormState(): VehicleFormState {
        return VehicleFormState(
            id = this.id,
            name = this.name,
            make = this.make,
            model = this.model,
            plateNumber = this.plateNumber,
            year = this.year.toString(),
            fuelTypes = this.fuelTypes.joinToString(", "),
            currentOdometer = this.currentOdometer.toString(),
            registrationDate = this.registrationDate,
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

    /**
     * Converts the VehicleFormState (from UI) back to a Vehicle domain model.
     */
    fun VehicleFormState.toVehicle(): Vehicle {
        return Vehicle(
            id = this.id ?: "",
            name = this.name,
            make = this.make,
            model = this.model,
            plateNumber = this.plateNumber,
            year = this.year.toIntOrNull() ?: 0,
            fuelTypes = this.fuelTypes.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            currentOdometer = this.currentOdometer.toIntOrNull() ?: 0,
            registrationDate = this.registrationDate,
            oilChangeKm = this.oilChangeKm.toLongOrNull() ?: 0,
            oilChangeDate = this.oilChangeDate.toLongOrNull() ?: 0,
            tiresChangeKm = this.tiresChangeKm.toLongOrNull() ?: 0,
            tiresChangeDate = this.tiresChangeDate.toLongOrNull() ?: 0,
            insuranceExpiryDate = this.insuranceExpiryDate.toLongOrNull() ?: 0,
            taxesExpiryDate = this.taxesExpiryDate.toLongOrNull() ?: 0,

        )
    }
