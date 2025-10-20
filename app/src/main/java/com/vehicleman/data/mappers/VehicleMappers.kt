// data/mappers/VehicleMappers.kt

package com.vehicleman.data.mappers

import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.model.Vehicle

fun VehicleEntity.toVehicle(): Vehicle {
    return Vehicle(
        id = id,
        registrationDate = registrationDate,
        make = make, // ΔΙΟΡΘΩΣΗ
        model = model,
        plateNumber = plateNumber, // ΔΙΟΡΘΩΣΗ
        year = year,
        fuelType = fuelType,
        currentOdometer = currentOdometer, // ΔΙΟΡΘΩΣΗ
        //... διόρθωσε και τα υπόλοιπα:
        oilChangeKm = oilChangeKm,
        oilChangeDate = oilChangeDate,
        tiresChangeKm = tiresChangeKm,
        tiresChangeDate = tiresChangeDate,
        insuranceExpiryDate = insuranceExpiryDate,
        taxesExpiryDate = taxesExpiryDate
    )
}

fun Vehicle.toVehicleEntity(): VehicleEntity {
    return VehicleEntity(
        id = id,
        registrationDate = registrationDate,
        make = make, // ΔΙΟΡΘΩΣΗ
        model = model,
        plateNumber = plateNumber, // ΔΙΟΡΘΩΣΗ
        fuelType = fuelType,
        year = year,
        currentOdometer = currentOdometer, // ΔΙΟΡΘΩΣΗ
        //... διόρθωσε και τα υπόλοιπα
        oilChangeKm = oilChangeKm,
        oilChangeDate = oilChangeDate,
        tiresChangeKm = tiresChangeKm,
        tiresChangeDate = tiresChangeDate,
        insuranceExpiryDate = insuranceExpiryDate,
        taxesExpiryDate = taxesExpiryDate,
    )
}