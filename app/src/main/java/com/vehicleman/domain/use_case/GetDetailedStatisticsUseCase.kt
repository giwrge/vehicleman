package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.DetailedStatistics
import com.vehicleman.domain.model.TimeFilter
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.util.VehicleStatisticsCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDetailedStatisticsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(vehicleId: String, timeFilter: TimeFilter): Flow<DetailedStatistics> {
        return recordRepository.getRecordsForVehicle(vehicleId).map { records ->
            VehicleStatisticsCalculator.calculateDetailed(records, timeFilter)
        }
    }

    operator fun invoke(vehicleIds: List<String>, timeFilter: TimeFilter): Flow<DetailedStatistics> {
        return recordRepository.getAllRecords().map { allRecords ->
            val filteredRecords = allRecords.filter { it.vehicleId in vehicleIds }
            VehicleStatisticsCalculator.calculateDetailed(filteredRecords, timeFilter)
        }
    }
}
