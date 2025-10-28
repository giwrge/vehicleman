package com.vehicleman.data.mappers

import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.model.Vehicle

fun VehicleEntity.toVehicle(): Vehicle {
    return Vehicle(
        id = id,
        name = name,
        make = make,
        model = model,
        plateNumber = plateNumber,
        year = year,
        fuelTypes = fuelTypes,
        currentOdometer = currentOdometer,
        registrationDate = registrationDate,
        oilChangeKm = oilChangeKm,
        oilChangeDate = oilChangeDate,
        tiresChangeKm = tiresChangeKm,
        tiresChangeDate = tiresChangeDate,
        insuranceExpiryDate = insuranceExpiryDate,
        taxesExpiryDate = taxesExpiryDate,
        dateAdded = dateAdded,
        lastModified = lastModified,
        recordCount = recordCount
    )
}

fun Vehicle.toVehicleEntity(): VehicleEntity {
    return VehicleEntity(
        id = id,
        name = name,
        make = make,
        model = model,
        plateNumber = plateNumber,
        year = year,
        fuelTypes = fuelTypes,
        currentOdometer = currentOdometer,
        registrationDate = registrationDate,
        oilChangeKm = oilChangeKm,
        oilChangeDate = oilChangeDate,
        tiresChangeKm = tiresChangeKm,
        tiresChangeDate = tiresChangeDate,
        insuranceExpiryDate = insuranceExpiryDate,
        taxesExpiryDate = taxesExpiryDate,
        dateAdded = dateAdded,
        lastModified = lastModified,
        recordCount = recordCount
    )
}
