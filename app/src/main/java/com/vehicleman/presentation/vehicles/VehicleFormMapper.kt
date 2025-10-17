package com.vehicleman.presentation.vehicles

import com.vehicleman.domain.model.Vehicle
import java.util.Date
import java.util.UUID

/** Μετατροπές μεταξύ UI state και Domain model **/

fun VehicleFormState.toVehicle(): Vehicle {
    return Vehicle(
        id = this.currentVehicle?.id ?: UUID.randomUUID().toString(),
        name = "${this.brand} ${this.model}".trim(),
        make = this.brand,
        model = this.model,
        year = this.year.toIntOrNull() ?: 0,
        licensePlate = this.plate,
        fuelType = this.fuelType,
        initialOdometer = this.odometer.toIntOrNull() ?: 0,
        registrationDate = this.currentVehicle?.registrationDate ?: Date(),
        oilChangeIntervalKm = this.oilChangeKm.toIntOrNull() ?: 10000,
        oilChangeIntervalDays = this.oilChangeTime.toIntOrNull() ?: 180
    )
}

fun Vehicle.toFormState(): VehicleFormState {
    return VehicleFormState(
        brand = this.make,
        model = this.model,
        plate = this.licensePlate,
        year = this.year.toString(),
        odometer = this.initialOdometer.toString(),
        oilChangeTime = this.oilChangeIntervalDays.toString(),
        oilChangeKm = this.oilChangeIntervalKm.toString(),
        tiresChangeTime = "",
        tiresChangeKm = "",
        insuranceDate = "",
        taxDate = "",
        fuelType = this.fuelType ?: "",
        currentVehicle = this
    )
}
