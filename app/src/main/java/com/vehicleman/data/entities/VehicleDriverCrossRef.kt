package com.vehicleman.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["id", "driverId"])
data class VehicleDriverCrossRef(
    val id: String, // Vehicle ID
    val driverId: String
)
