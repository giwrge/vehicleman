package com.vehicleman.domain.use_case

import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import javax.inject.Inject

/**
 * Use Case για τον καθαρισμό όλων των δεδομένων της εφαρμογής
 * χωρίς τη διαγραφή των οχημάτων.
 */
class ClearAllDataExceptVehiclesUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke() {
        // 1. Διαγραφή όλων των εγγραφών (Service, Καύσιμα, Υπενθυμίσεις)
        recordRepository.deleteAllRecords()
        
        // 2. Διαγραφή όλων των οδηγών
        driverRepository.deleteAllDrivers()
        
        // 3. Διαγραφή των συσχετίσεων μεταξύ οχημάτων και οδηγών
        driverRepository.deleteAllCrossRefs()
        
        // 4. Επαναφορά του μετρητή εγγραφών (recordCount) για όλα τα οχήματα
        val vehicles = vehicleRepository.getAllVehiclesList()
        vehicles.forEach { vehicle ->
            vehicleRepository.updateVehicle(vehicle.copy(recordCount = 0))
        }
    }
}
