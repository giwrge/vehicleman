package com.vehicleman.data.mappers

import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.domain.model.Record

fun RecordEntity.toRecord(): Record {
    return Record(
        id = id,
        vehicleId = vehicleId,
        date = date,
        odometer = odometer,
        isExpense = isExpense,
        title = title,
        description = description,
        amount = amount,
        reminderDate = reminderDate
    )
}

fun Record.toRecordEntity(): RecordEntity {
    return RecordEntity(
        id = id,
        vehicleId = vehicleId,
        date = date,
        odometer = odometer,
        isExpense = isExpense,
        title = title,
        description = description,
        amount = amount,
        reminderDate = reminderDate
    )
}
