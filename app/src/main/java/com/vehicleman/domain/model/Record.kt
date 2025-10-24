package com.vehicleman.domain.model

import java.util.UUID

data class Record(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val date: Long,
    val odometer: Int,
    val isExpense: Boolean,
    val title: String,
    val description: String?,
    val amount: Double?,
    val reminderDate: Long?
)
