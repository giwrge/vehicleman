package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "driverId"],
    indices = [Index(value = ["driverId"])]
)
data class VehicleDriverCrossRef(
    val id: String, // Vehicle ID
    val driverId: String
)
