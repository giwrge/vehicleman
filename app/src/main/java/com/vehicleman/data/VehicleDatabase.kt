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
 *
 * Περιλαμβάνει:
 *  - VehicleEntity → Πληροφορίες Οχημάτων
 *  - RecordEntity → Συντηρήσεις / Καταγραφές
 */
@Database(
    entities = [
        VehicleEntity::class,
        RecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class VehicleDatabase : RoomDatabase() {

    /** DAO για τα Οχήματα */
    abstract fun vehicleDao(): VehicleDao

    /** DAO για τις Εγγραφές Συντήρησης */
    abstract fun recordDao(): RecordDao

    companion object {
        const val DATABASE_NAME = "vehicle_man_db"
    }
}
