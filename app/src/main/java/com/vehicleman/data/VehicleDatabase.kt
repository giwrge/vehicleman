package com.vehicleman.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // Χρειαζόμαστε converters για την Date
import com.vehicleman.data.dao.EntryDao // ΝΕΟ
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.EntryEntity // ΝΕΟ
import com.vehicleman.data.entities.VehicleEntity

/**
 * Main database class for the application.
 */
@Database(
    entities = [
        VehicleEntity::class,
        EntryEntity::class // Προσθέτουμε το EntryEntity
        // Επίσης, πρέπει να προσθέσετε το ExpenseEntity αν είναι ξεχωριστό
    ],
    version = 1, // Ίσως χρειαστεί αύξηση αν είχατε παλιότερη δομή
    exportSchema = false
)
// Θα χρειαστείτε έναν Type Converter για να αποθηκεύσετε την java.util.Date
@TypeConverters(DateConverter::class) // ΥΠΟΘΕΤΩ ΥΠΑΡΞΗ DateConverter
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun entryDao(): EntryDao // ΝΕΟ
    // abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "vehicle_database"
    }
}
