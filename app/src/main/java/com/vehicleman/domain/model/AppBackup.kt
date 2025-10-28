package com.vehicleman.domain.model

import com.vehicleman.data.entities.DriverEntity
import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.data.entities.VehicleDriverCrossRef
import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.repositories.User

/**
 * A data class that holds all the data for a full application backup.
 */
data class AppBackup(
    val user: User,
    val vehicles: List<VehicleEntity>,
    val records: List<RecordEntity>,
    val drivers: List<DriverEntity>,
    val vehicleDriverRelations: List<VehicleDriverCrossRef>
)
