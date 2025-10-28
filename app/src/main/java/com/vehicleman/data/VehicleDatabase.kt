package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vehicleman.data.dao.RecordDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.data.entities.VehicleEntity

/**
 * Κεντρική Room Database της εφαρμογής VehicleMan.
 */
@Database(
    entities = [VehicleEntity::class, RecordEntity::class],
    version = 4, // Incremented version due to schema change in RecordEntity
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    abstract fun recordDao(): RecordDao

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"
    }
}
