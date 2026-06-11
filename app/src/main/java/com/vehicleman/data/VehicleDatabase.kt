package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 8, // Incremented version again to force recreation
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    abstract fun recordDao(): RecordDao

    abstract fun driverDao(): DriverDao

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Προσθήκη του νέου πεδίου costReminder στην υπάρχουσα βάση
                db.execSQL("ALTER TABLE records ADD COLUMN costReminder REAL")
            }
        }
    }
}
