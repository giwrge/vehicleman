package com.vehicleman.domain.repositories

import com.vehicleman.domain.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {

    fun getRecordsForVehicle(vehicleId: String): Flow<List<Record>>
    suspend fun getRecordsByVehicle(vehicleId: String): List<Record>

    suspend fun getRecordById(id: String): Record?

    suspend fun saveRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    suspend fun getLatestOdometer(vehicleId: String): Int?

    // ✅ Fuel specific
    suspend fun getLastFuelUpRecord(vehicleId: String): Record?

    fun getAllRecords(): Flow<List<Record>>

    // For Backup/Restore
    suspend fun getAllRecordsList(): List<Record>
    suspend fun deleteAllRecords()
    suspend fun insertAllRecords(records: List<Record>)
}
