// app/src/main/java/com/vehicleman/data/VehicleDatabase.kt
package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vehicleman.data.dao.MaintenanceRecordDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.MaintenanceRecordEntity
import com.vehicleman.data.entities.VehicleEntity

/**
 * Main database class for the application.
 */
@Database(
    entities = [
        VehicleEntity::class,
        MaintenanceRecordEntity::class // ΣΥΜΠΕΡΙΛΗΨΗ του νέου Entity
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceRecordDao(): MaintenanceRecordDao // ΠΡΟΣΘΗΚΗ του DAO

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"
    }
}