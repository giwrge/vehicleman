package com.vehicleman.domain.model

import java.util.Date
import java.util.UUID

data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val brand: String = "",
    val model: String = "",
    val plate: String = "",
    val year: Int = 0,
    val fuelType: String = "",
    val odometer: Int = 0,
    val registrationDate: Date = Date(),
    val oilChangeTime: String = "",
    val oilChangeKm: Int = 0,
    val tiresChangeTime: String = "",
    val tiresChangeKm: Int = 0,
    val insurancePaymentDate: String = "",
    val taxesPaymentDate: String = ""
)
