// app/src/main/java/com/vehicleman/data/local/AppDatabase.kt

package com.vehicleman.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vehicleman.data.dao.ExpenseDao
import com.vehicleman.data.local.entities.Expense
import com.vehicleman.data.local.entities.User
import com.vehicleman.data.local.entities.Vehicle

@Database(
    entities = [Vehicle::class, Expense::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "car_manager_db"
    }
}