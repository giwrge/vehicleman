package com.vehicleman.domain.use_case

import com.vehicleman.domain.repositories.RecordRepository
import javax.inject.Inject

class GetLatestOdometer @Inject constructor(
    private val repository: RecordRepository
) {
    suspend operator fun invoke(vehicleId: String): Int? {
        return repository.getLatestOdometer(vehicleId)
    }
}
