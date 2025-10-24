package com.vehicleman.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vehicleman.data.entities.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    /** Gets all entries for a specific vehicle, ordered by date. */
    @Query("SELECT * FROM records WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getEntriesForVehicle(vehicleId: String): Flow<List<RecordEntity>>

    /** Gets a single entry by its ID. */
    @Query("SELECT * FROM records WHERE id = :entryId")
    suspend fun getEntryById(entryId: String): RecordEntity?

    /** Gets the latest odometer reading for a specific vehicle. */
    @Query("SELECT odometer FROM records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC LIMIT 1")
    suspend fun getLatestOdometer(vehicleId: String): Int?

    /** Inserts or updates an entry. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: RecordEntity)

    /** Deletes an entry by its ID. */
    @Query("DELETE FROM records WHERE id = :entryId")
    suspend fun deleteEntry(entryId: String)

    /** Deletes all entries associated with a vehicle. */
    @Query("DELETE FROM records WHERE vehicleId = :vehicleId")
    suspend fun deleteEntriesByVehicleId(vehicleId: String)
}