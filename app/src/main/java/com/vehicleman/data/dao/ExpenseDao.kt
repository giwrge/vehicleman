// app/src/main/java/com/vehicleman/data/local/dao/ExpenseDao.kt

/*package com.vehicleman.data.dao

import androidx.room.*
import com.vehicleman.data.entities.ExpenseEntity
import com.vehicleman.data.entities.UserEntity
import com.vehicleman.data.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // --- VEHICLE OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles ORDER BY name ASC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    // --- EXPENSE OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY entryDate DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    // --- USER OPERATIONS (PRO Feature) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>
}*/