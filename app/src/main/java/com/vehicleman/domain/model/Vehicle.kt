package com.vehicleman.domain.model

import java.util.Date
import java.util.UUID

data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val licensePlate: String = "",
    val fuelType: String = "",
    val initialOdometer: Int = 0,
    val registrationDate: Date = Date(),
    val oilChangeIntervalKm: Int = 10000,
    val oilChangeIntervalDays: Int = 180
)

