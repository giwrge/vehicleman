package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vehicleman.data.dao.DriverDao
import com.vehicleman.data.dao.RecordDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.DriverEntity
import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.data.entities.VehicleDriverCrossRef
import com.vehicleman.data.entities.VehicleEntity

/**
 * Κεντρική Room Database της εφαρμογής VehicleMan.
 */
@Database(
    entities = [VehicleEntity::class, RecordEntity::class, DriverEntity::class, VehicleDriverCrossRef::class],
    version = 6, // Incremented version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    abstract fun recordDao(): RecordDao

    abstract fun driverDao(): DriverDao

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"
    }
}
