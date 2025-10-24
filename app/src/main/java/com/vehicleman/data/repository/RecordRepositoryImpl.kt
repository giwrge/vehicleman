package com.vehicleman.data.repository

import com.vehicleman.data.dao.RecordDao
import com.vehicleman.data.entities.RecordEntity
import com.vehicleman.data.mappers.toRecord
import com.vehicleman.data.mappers.toRecordEntity
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
) : RecordRepository {

    override fun getRecordsForVehicle(vehicleId: String): Flow<List<Record>> {
        return recordDao.getEntriesForVehicle(vehicleId).map { list -> list.map(RecordEntity::toRecord) }
    }

    override suspend fun getRecordById(id: String): Record? {
        return recordDao.getEntryById(id)?.toRecord()
    }

    override suspend fun saveRecord(record: Record) {
        recordDao.insertEntry(record.toRecordEntity())
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.deleteEntry(record.id)
    }

    override suspend fun deleteRecordsByVehicleId(vehicleId: String) {
        recordDao.deleteEntriesByVehicleId(vehicleId)
    }

    override suspend fun getLatestOdometer(vehicleId: String): Int? {
        return recordDao.getLatestOdometer(vehicleId)
    }
}
