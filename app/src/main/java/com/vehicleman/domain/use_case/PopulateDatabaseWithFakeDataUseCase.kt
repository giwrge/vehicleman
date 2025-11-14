package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class PopulateDatabaseWithFakeDataUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke() {
        val vehicles = vehicleRepository.getAllVehiclesList()
        if (vehicles.isEmpty()) return // Don't run if there are no vehicles

        vehicles.forEach { vehicle ->
            val calendar = Calendar.getInstance()
            var currentOdometer = 50000 // Start from 50k km

            // --- Generate 15 past expenses over 2 years ---
            val expenseTitles = listOf("Service", "New Tires", "Oil Change", "Fuel Up", "Brake Pads", "Wiper Blades", "Air Filter")
            repeat(15) {
                // Random date in the last 2 years
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -Random.nextInt(0, 365 * 2))
                val expenseDate = calendar.time

                // Increment odometer
                currentOdometer += Random.nextInt(500, 3000)
                
                val record = Record(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    recordType = RecordType.EXPENSE,
                    title = expenseTitles.random(),
                    description = "Fake generated data",
                    date = expenseDate,
                    odometer = currentOdometer,
                    cost = Random.nextDouble(20.0, 500.0),
                    quantity = if (it % 4 == 0) Random.nextDouble(30.0, 50.0) else null, // occasionally add fuel quantity
                    pricePerUnit = if (it % 4 == 0) Random.nextDouble(1.8, 2.2) else null,
                    fuelType = if (it % 4 == 0) listOf("Unleaded", "Diesel").random() else null,
                    isReminder = false,
                    reminderDate = null,
                    reminderOdometer = null
                )
                recordRepository.saveRecord(record)
            }

            // --- Generate 15 future reminders over 4 years ---
            val reminderTitles = listOf("KTEO Inspection", "Insurance Renewal", "Annual Service", "Tire Rotation", "Tax Payment", "Check Coolant")
            repeat(15) {
                // Random date in the next 4 years
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(1, 365 * 4))
                val reminderDate = calendar.time

                val record = Record(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    recordType = RecordType.REMINDER,
                    title = reminderTitles.random(),
                    description = "Fake generated reminder",
                    date = calendar.time, // Date of creation is now
                    odometer = 0, // Not relevant for future reminders
                    cost = null,
                    quantity = null,
                    pricePerUnit = null,
                    fuelType = null,
                    isReminder = true,
                    reminderDate = reminderDate,
                    reminderOdometer = currentOdometer + Random.nextInt(5000, 20000) // Estimate a future odometer
                )
                recordRepository.saveRecord(record)
            }
        }
    }
}
