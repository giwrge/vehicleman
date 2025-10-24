package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.VehicleConsumption
import com.vehicleman.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetVehicleConsumption @Inject constructor(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(vehicleId: String): Flow<VehicleConsumption> {
        // TODO: Replace with actual implementation
        return recordRepository.getRecordsForVehicle(vehicleId).map {
            VehicleConsumption(
                totalConsumption = 0.0,
                consumptionPerFuelType = emptyMap()
            )
        }
    }
}
