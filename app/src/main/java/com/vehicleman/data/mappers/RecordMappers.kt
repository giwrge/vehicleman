package com.vehicleman.data.mappers

import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.domain.model.RecordExpenseCategory
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType

fun RecordEntity.toRecord(): Record {
    return Record(
        id = id,
        vehicleId = vehicleId,
        recordType = runCatching { RecordType.valueOf(recordType) }.getOrElse { RecordType.EXPENSE },
        category = category?.let { 
            runCatching { RecordExpenseCategory.valueOf(it) }.getOrNull()
        },
        title = title,
        description = description,
        date = date,
        odometer = odometer,
        cost = cost,
        quantity = quantity,
        pricePerUnit = pricePerUnit,
        fuelType = fuelType,
        isFullTank = isFullTank,
        isReminder = isReminder,
        reminderDate = reminderDate,
        reminderOdometer = reminderOdometer,
        isCompleted = isCompleted,
        costReminder = costReminder
    )
}

fun Record.toRecordEntity(): RecordEntity {
    return RecordEntity(
        id = id,
        vehicleId = vehicleId,
        recordType = recordType.name,
        category = category?.name,
        title = title,
        description = description,
        date = date,
        odometer = odometer,
        cost = cost,
        quantity = quantity,
        pricePerUnit = pricePerUnit,
        fuelType = fuelType,
        isFullTank = isFullTank,
        isReminder = isReminder,
        reminderDate = reminderDate,
        reminderOdometer = reminderOdometer,
        costReminder = costReminder,
        isCompleted = isCompleted
    )
}
