package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecords @Inject constructor(
    private val repository: RecordRepository
) {
    operator fun invoke(vehicleId: String): Flow<List<Record>> {
        return repository.getRecordsForVehicle(vehicleId)
    }
}
