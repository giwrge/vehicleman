package com.vehicleman.data.mappers

import com.vehicleman.data.entities.DriverEntity
import com.vehicleman.domain.model.Driver

fun DriverEntity.toDriver(): Driver {
    return Driver(
        driverId = driverId,
        name = name
    )
}

fun Driver.toDriverEntity(): DriverEntity {
    return DriverEntity(
        driverId = driverId,
        name = name
    )
}
