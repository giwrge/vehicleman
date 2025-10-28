package com.vehicleman.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DriverWithVehicles(
    @Embedded val driver: DriverEntity,
    @Relation(
        parentColumn = "driverId",
        entityColumn = "id",
        associateBy = Junction(VehicleDriverCrossRef::class)
    )
    val vehicles: List<VehicleEntity>
)
