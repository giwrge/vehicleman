package com.vehicleman.domain.model

import java.util.Date

data class Vehicle(
    val id: String,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val fuelType: String,
    val initialOdometer: Int,
    val registrationDate: Date,
    val oilChangeIntervalKm: Int = 10000,
    val oilChangeIntervalDays: Int = 365
) {
    val isNew: Boolean
        get() = id.isBlank() || id == "new"
}