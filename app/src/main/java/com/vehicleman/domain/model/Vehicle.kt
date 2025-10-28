package com.vehicleman.domain.model

import java.util.Date
import java.util.UUID

data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val plateNumber: String = "",
    val year: Int = 0,
    val fuelTypes: List<String> = emptyList(), // Changed from fuelType: String
    val currentOdometer: Int = 0,
    val registrationDate: Date = Date(),
    val oilChangeKm: Long = 10000,
    val oilChangeDate: Long = 364,
    val tiresChangeKm: Long = 40000,
    val tiresChangeDate: Long = 364,
    val insuranceExpiryDate: Long = 364,
    val taxesExpiryDate: Long = 364,

)

