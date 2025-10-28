package com.vehicleman.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert // Η Upsert είναι ιδανική για save/update
import kotlinx.coroutines.flow.Flow
import com.vehicleman.data.entities.RecordEntity
@Dao
interface RecordDao {

    // Μια μέθοδος για αποθήκευση/ενημέρωση. Η Upsert κάνει insert ή update αυτόματα.
    @Upsert
    suspend fun upsertRecord(record: RecordEntity)

    // Παίρνει μια συγκεκριμένη εγγραφή
    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: String): RecordEntity?

    // Παίρνει ΟΛΕΣ τις εγγραφές για ένα όχημα, ταξινομημένες.
    // Χρησιμοποίησε Flow για να παίρνεις αυτόματα τις ενημερώσεις!
    @Query("SELECT * FROM records WHERE vehicleId = :vehicleId ORDER BY date DESC, odometer DESC")
    fun getRecordsForVehicle(vehicleId: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>
    
    @Query("SELECT * FROM records")
    suspend fun getAllRecordsList(): List<RecordEntity>
    
    @Query("DELETE FROM records")
    suspend fun deleteAllRecords()

    // Παίρνει μόνο τις ενεργές υπενθυμίσεις
    @Query("SELECT * FROM records WHERE vehicleId = :vehicleId AND isReminder = 1 AND isCompleted = 0 ORDER BY reminderDate ASC")
    fun getActiveReminders(vehicleId: String): Flow<List<RecordEntity>>

    // Ένα query για τα στατιστικά σου!
    @Query("SELECT SUM(cost) FROM records WHERE vehicleId = :vehicleId AND recordType = 'FUEL_UP'")
    fun getTotalFuelCost(vehicleId: String): Flow<Double?>

    // Παίρνει το τελευταίο χιλιόμετρο που καταχωρήθηκε
    @Query("SELECT MAX(odometer) FROM records WHERE vehicleId = :vehicleId")
    suspend fun getLatestOdometer(vehicleId: String): Int?

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecordById(id: String)
}
