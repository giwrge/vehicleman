package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.repositories.RecordRepository
import javax.inject.Inject

class GetLastFuelUpRecord @Inject constructor(
    private val repository: RecordRepository
) {
    suspend operator fun invoke(vehicleId: String): Record? {
        return repository.getLastFuelUpRecord(vehicleId)
    }
}