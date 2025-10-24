package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.VehicleStatistics
import com.vehicleman.domain.repositories.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetVehicleStatistics @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): Flow<List<VehicleStatistics>> {
        // TODO: Replace with actual implementation
        return vehicleRepository.getAllVehicles().map {
            it.map {
                VehicleStatistics(
                    vehicleId = it.id,
                    vehicleName = it.name,
                    costPerMonth = 0.0,
                    costPerDay = 0.0
                )
            }
        }
    }
}
