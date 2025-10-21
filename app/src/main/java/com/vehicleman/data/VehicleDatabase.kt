package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.VehicleEntity

/**
 * Κεντρική Room Database της εφαρμογής VehicleMan.
 */
@Database(
    entities = [VehicleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    // abstract fun recordDao(): com.vehicleman.data.dao.RecordDao // Temporarily disabled

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"
    }
}
