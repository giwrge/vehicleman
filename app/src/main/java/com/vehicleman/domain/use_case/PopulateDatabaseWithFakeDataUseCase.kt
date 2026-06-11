package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.RecordExpenseCategory
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
        if (vehicles.isEmpty()) return

        vehicles.forEach { vehicle ->
            val calendar = Calendar.getInstance()
            var currentOdometer = 50000

            // --- Generate 15 past expenses ---
            val expenseTitles = listOf("Service", "New Tires", "Oil Change", "Fuel Up", "Brake Pads", "Wiper Blades", "Air Filter")
            repeat(15) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -Random.nextInt(0, 365 * 2))
                val expenseDate = calendar.time
                currentOdometer += Random.nextInt(500, 3000)
                
                val isFuel = it % 4 == 0
                val record = Record(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    recordType = if (isFuel) RecordType.FUEL_UP else RecordType.EXPENSE,
                    category = if (isFuel) RecordExpenseCategory.FUEL else RecordExpenseCategory.SERVICE,
                    title = if (isFuel) "Fuel Up" else expenseTitles.random(),
                    description = "Fake generated data",
                    date = expenseDate,
                    odometer = currentOdometer,
                    cost = Random.nextDouble(20.0, 500.0),
                    quantity = if (isFuel) Random.nextDouble(30.0, 50.0) else null,
                    pricePerUnit = if (isFuel) Random.nextDouble(1.8, 2.2) else null,
                    fuelType = if (isFuel) listOf("Unleaded", "Diesel").random() else null,
                    isReminder = false,
                    reminderDate = null,
                    reminderOdometer = null,
                    isCompleted = true,
                    costReminder = null
                )
                recordRepository.saveRecord(record)
            }

            // --- Generate 15 future reminders ---
            val reminderTitles = listOf("KTEO Inspection", "Insurance Renewal", "Annual Service", "Tire Rotation", "Tax Payment", "Check Coolant")
            repeat(15) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(1, 365 * 4))
                val reminderDate = calendar.time

                val record = Record(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    recordType = RecordType.REMINDER,
                    category = RecordExpenseCategory.OTHER,
                    title = reminderTitles.random(),
                    description = "Fake generated reminder",
                    date = Date(),
                    odometer = currentOdometer,
                    cost = null,
                    quantity = null,
                    pricePerUnit = null,
                    fuelType = null,
                    isReminder = true,
                    reminderDate = reminderDate,
                    reminderOdometer = currentOdometer + Random.nextInt(5000, 20000),
                    isCompleted = false,
                    costReminder = Random.nextDouble(50.0, 200.0)
                )
                recordRepository.saveRecord(record)
            }
        }
    }
}
