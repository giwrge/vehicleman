package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.repositories.RecordRepository
import javax.inject.Inject

class SaveRecord @Inject constructor(
    private val repository: RecordRepository
) {
    suspend operator fun invoke(record: Record) {
        repository.saveRecord(record)
    }
}
