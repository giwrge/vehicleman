package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vehicleman.data.dao.EntryDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.EntryEntity
import com.vehicleman.data.entities.VehicleEntity

/**
 * Η κεντρική βάση δεδομένων Room για την εφαρμογή VehicleMan.
 *
 * Περιλαμβάνει τις οντότητες (πίνακες) οχημάτων (VehicleEntity)
 * και συμβάντων (EntryEntity).
 *
 * Έκδοση 2: Περιλαμβάνει πλέον και τα EntryEntity.
 */
@Database(
    entities = [VehicleEntity::class, EntryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun entryDao(): EntryDao

    companion object {
        const val DATABASE_NAME = "vehicle_database"
    }
}
