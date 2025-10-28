package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val driverId: String = UUID.randomUUID().toString(),
    val name: String
)
