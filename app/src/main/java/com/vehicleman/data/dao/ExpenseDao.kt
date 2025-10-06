// app/src/main/java/com/vehicleman/data/local/dao/ExpenseDao.kt

package com.vehicleman.data.dao

import androidx.room.*
import com.vehicleman.data.entities.Expense
import com.vehicleman.data.entities.User
import com.vehicleman.data.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // --- VEHICLE OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles ORDER BY name ASC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    // --- EXPENSE OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY entryDate DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // --- USER OPERATIONS (PRO Feature) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>
}