package com.vehicleman.data.mappers

import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.model.Vehicle

fun VehicleEntity.toDomain() = Vehicle(
    id = id,
    name = name,
    make = make,
    model = model,
    year = year,
    licensePlate = licensePlate,
    fuelType = fuelType,
    initialOdometer = initialOdometer,
    registrationDate = registrationDate,
    oilChangeIntervalKm = oilChangeIntervalKm,
    oilChangeIntervalDays = oilChangeIntervalDays
)

fun Vehicle.toEntity() = VehicleEntity(
    id = id,
    name = name,
    make = make,
    model = model,
    year = year,
    licensePlate = licensePlate,
    fuelType = fuelType,
    initialOdometer = initialOdometer,
    registrationDate = registrationDate,
    oilChangeIntervalKm = oilChangeIntervalKm,
    oilChangeIntervalDays = oilChangeIntervalDays
)