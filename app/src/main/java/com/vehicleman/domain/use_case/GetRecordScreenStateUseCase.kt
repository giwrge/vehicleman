package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.record.RecordState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date
import javax.inject.Inject

class GetRecordScreenStateUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val recordRepository: RecordRepository
) {
    operator fun invoke(vehicleId: String): Flow<RecordState> {
        val vehiclesFlow = vehicleRepository.getAllVehicles()
        val recordsFlow = recordRepository.getRecordsForVehicle(vehicleId)

        return combine(vehiclesFlow, recordsFlow) { vehicles, records ->

            // Sort all items ascending: from the furthest past to the furthest future.
            val timelineItems = records.sortedBy { 
                if (it.isReminder) it.reminderDate else it.date 
            }

            RecordState(
                vehicles = vehicles,
                selectedVehicleId = vehicleId,
                timelineItems = timelineItems
            )
        }
    }
}
