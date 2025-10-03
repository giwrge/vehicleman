package com.vehicleman.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vehicleman.data.entities.EntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) για αλληλεπίδραση με τον πίνακα 'entries'.
 */
@Dao
interface EntryDao {

    /**
     * Εισάγει ένα νέο συμβάν στη βάση (ή ενημερώνει αν υπάρχει).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity)

    /**
     * Διαγράφει ένα συμβάν.
     */
    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    /**
     * Λαμβάνει όλα τα συμβάντα για ένα συγκεκριμένο όχημα, ταξινομημένα
     * αντίστροφα χρονολογικά (πιο πρόσφατα πρώτα).
     */
    @Query("SELECT * FROM entries WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getEntriesForVehicle(vehicleId: String): Flow<List<EntryEntity>>

    /**
     * Λαμβάνει ένα συμβάν με βάση το ID του.
     */
    @Query("SELECT * FROM entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: String): EntryEntity?

    /**
     * Διαγράφει όλα τα συμβάντα για ένα συγκεκριμένο όχημα.
     * Χρησιμοποιείται όταν διαγράφεται το όχημα.
     */
    @Query("DELETE FROM entries WHERE vehicleId = :vehicleId")
    suspend fun deleteEntriesForVehicle(vehicleId: String)
}
