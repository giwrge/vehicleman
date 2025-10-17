package com.vehicleman.data.mappers

import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.model.Vehicle

fun VehicleEntity.toDomain() = Vehicle(
    id = id,
    brand = brand,
    model = model,
    plate = plate,
    year = year,
    fuelType = fuelType,
    odometer = odometer,
    registrationDate = registrationDate,
    oilChangeTime = oilChangeTime,
    oilChangeKm = oilChangeKm,
    tiresChangeTime = tiresChangeTime,
    tiresChangeKm = tiresChangeKm,
    insurancePaymentDate = insurancePaymentDate,
    taxesPaymentDate = taxesPaymentDate
)

fun Vehicle.toEntity() = VehicleEntity(
    id = id,
    brand = brand,
    model = model,
    plate = plate,
    year = year,
    fuelType = fuelType,
    odometer = odometer,
    registrationDate = registrationDate,
    oilChangeTime = oilChangeTime,
    oilChangeKm = oilChangeKm,
    tiresChangeTime = tiresChangeTime,
    tiresChangeKm = tiresChangeKm,
    insurancePaymentDate = insurancePaymentDate,
    taxesPaymentDate = taxesPaymentDate
)
