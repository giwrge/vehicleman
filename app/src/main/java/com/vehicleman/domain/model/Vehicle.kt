package com.vehicleman.domain.model

/**
 * Domain model representing a vehicle, used in the UI layer.
 * Note: This model should ideally be mapped from VehicleEntity.
 */
data class Vehicle(
    val id: String,
    val name: String,
    val make: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
    val fuelType: String,
    val initialOdometer: Int,
    val registrationDate: Long
)
