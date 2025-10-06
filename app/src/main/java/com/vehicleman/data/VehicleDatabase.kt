package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vehicleman.data.dao.EntryDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.dao.ExpenseDao
import com.vehicleman.data.entities.EntryEntity
import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.data.entities.ExpenseEntity
import com.vehicleman.data.entities.UserEntity


/**
 * Η κεντρική βάση δεδομένων Room για την εφαρμογή VehicleMan.
 *
 * Περιλαμβάνει τις οντότητες (πίνακες) οχημάτων (VehicleEntity)
 * και συμβάντων (EntryEntity).
 *
 * Έκδοση 2: Περιλαμβάνει πλέον και τα EntryEntity.
 */
@Database(
    entities = [VehicleEntity::class, EntryEntity::class, ExpenseEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun entryDao(): EntryDao

    abstract fun expenseDao(): ExpenseDao


    companion object {
        const val DATABASE_NAME = "vehicle_database"
    }
}
