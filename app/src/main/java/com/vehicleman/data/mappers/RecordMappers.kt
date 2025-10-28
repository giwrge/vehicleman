package com.vehicleman.data.mappers

import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType

fun RecordEntity.toRecord(): Record {
    return Record(
        id = id,
        vehicleId = vehicleId,
        // Χρησιμοποιούμε έναν ασφαλή τρόπο μετατροπής του String σε Enum
        recordType = try {
            RecordType.valueOf(recordType)
        } catch (e: IllegalArgumentException) {
            RecordType.EXPENSE // Ένα λογικό default σε περίπτωση σφάλματος
        },
        title = title,
        description = description,
        date = date,
        odometer = odometer,
        cost = cost,
        quantity = quantity,
        pricePerUnit = pricePerUnit,
        fuelType = fuelType,
        isReminder = isReminder,
        reminderDate = reminderDate,
        reminderOdometer = reminderOdometer,
        isCompleted = isCompleted
    )
}

fun Record.toRecordEntity(): RecordEntity {
    return RecordEntity(
        id = id,
        vehicleId = vehicleId,
        recordType = recordType.name, // Μετατρέπουμε το Enum σε String για αποθήκευση
        title = title,
        description = description,
        date = date,
        odometer = odometer,
        cost = cost,
        quantity = quantity,
        pricePerUnit = pricePerUnit,
        fuelType = fuelType,
        isReminder = isReminder,
        reminderDate = reminderDate,
        reminderOdometer = reminderOdometer,
        isCompleted = isCompleted
    )
}
