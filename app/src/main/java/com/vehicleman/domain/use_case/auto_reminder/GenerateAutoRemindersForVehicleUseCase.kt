package com.vehicleman.domain.use_case.auto_reminder

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.RecordExpenseCategory
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.RecordRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GenerateAutoRemindersForVehicleUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle,
        existingRecords: List<Record>
    ): List<Record> {

        val newReminders = mutableListOf<Record>()

        // 1. Αλλαγή λαδιών — KM
        if (vehicle.oilChangeKm > 0L) {
            val nextCycle = vehicle.currentOdometer / vehicle.oilChangeKm
            val dueKm = (nextCycle + 1) * vehicle.oilChangeKm

            val exists = existingRecords.any {
                it.isReminder &&
                        it.title.contains("Αλλαγή λαδιών", ignoreCase = true) &&
                        (it.reminderOdometer ?: -1) == dueKm.toInt()
            }

            if (!exists) {
                newReminders += buildKmReminder(vehicle, "Αλλαγή λαδιών", dueKm)
            }
        }

        // 2. Αλλαγή λαδιών — Date
        if (vehicle.oilChangeDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.oilChangeDate.toInt())
            val exists = existingRecords.any {
                it.isReminder && it.title.contains("Αλλαγή λαδιών", ignoreCase = true) && isSameDay(it.reminderDate, dueDate)
            }
            if (!exists) newReminders += buildDateReminder(vehicle, "Αλλαγή λαδιών", dueDate)
        }

        // 3. Αλλαγή ελαστικών — KM
        if (vehicle.tiresChangeKm > 0L) {
            val nextCycle = vehicle.currentOdometer / vehicle.tiresChangeKm
            val dueKm = (nextCycle + 1) * vehicle.tiresChangeKm
            val exists = existingRecords.any {
                it.isReminder && it.title.contains("Αλλαγή ελαστικών", ignoreCase = true) && (it.reminderOdometer ?: -1) == dueKm.toInt()
            }
            if (!exists) newReminders += buildKmReminder(vehicle, "Αλλαγή ελαστικών", dueKm)
        }

        // 4. Αλλαγή ελαστικών — Date
        if (vehicle.tiresChangeDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.tiresChangeDate.toInt())
            val exists = existingRecords.any {
                it.isReminder && it.title.contains("Αλλαγή ελαστικών", ignoreCase = true) && isSameDay(it.reminderDate, dueDate)
            }
            if (!exists) newReminders += buildDateReminder(vehicle, "Αλλαγή ελαστικών", dueDate)
        }

        // 5. Ασφάλεια
        if (vehicle.insuranceExpiryDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.insuranceExpiryDate.toInt())
            val exists = existingRecords.any {
                it.isReminder && it.title.contains("Ασφάλεια", ignoreCase = true) && isSameDay(it.reminderDate, dueDate)
            }
            if (!exists) newReminders += buildDateReminder(vehicle, "Ασφάλεια", dueDate)
        }

        // 6. Τέλη κυκλοφορίας
        val roadTaxDate = fixedDayMonth(27, Calendar.DECEMBER)
        val existsRoadTax = existingRecords.any {
            it.isReminder && it.title.contains("Τέλη κυκλοφορίας", ignoreCase = true) && isSameDay(it.reminderDate, roadTaxDate)
        }
        if (!existsRoadTax) newReminders += buildDateReminder(vehicle, "Τέλη κυκλοφορίας", roadTaxDate)

        // 7. ΚΤΕΟ
        val kteoDueDate = addYears(vehicle.registrationDate, 2)
        val existsKteo = existingRecords.any {
            it.isReminder && it.title.contains("ΚΤΕΟ", ignoreCase = true) && isSameDay(it.reminderDate, kteoDueDate)
        }
        if (!existsKteo) newReminders += buildDateReminder(vehicle, "ΚΤΕΟ", kteoDueDate)

        for (r in newReminders) {
            recordRepository.saveRecord(r)
        }

        return newReminders
    }

    private fun buildKmReminder(vehicle: Vehicle, title: String, dueKm: Long): Record {
        return Record(
            id = java.util.UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            recordType = RecordType.REMINDER,
            category = RecordExpenseCategory.OTHER,
            title = title,
            description = null,
            date = Date(),
            odometer = vehicle.currentOdometer,
            cost = null,
            quantity = null,
            pricePerUnit = null,
            fuelType = null,
            isReminder = true,
            reminderDate = null,
            reminderOdometer = dueKm.toInt(),
            isCompleted = false,
            costReminder = null
        )
    }

    private fun buildDateReminder(vehicle: Vehicle, title: String, dueDate: Date): Record {
        return Record(
            id = java.util.UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            recordType = RecordType.REMINDER,
            category = RecordExpenseCategory.OTHER,
            title = title,
            description = null,
            date = Date(),
            odometer = vehicle.currentOdometer,
            cost = null,
            quantity = null,
            pricePerUnit = null,
            fuelType = null,
            isReminder = true,
            reminderDate = dueDate,
            reminderOdometer = null,
            isCompleted = false,
            costReminder = null
        )
    }

    private fun addDays(start: Date, days: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = start
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.time
    }

    private fun addYears(start: Date, years: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = start
        cal.add(Calendar.YEAR, years)
        return cal.time
    }

    private fun fixedDayMonth(day: Int, month: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.MONTH, month)
        return cal.time
    }

    private fun isSameDay(a: Date?, b: Date?): Boolean {
        if (a == null || b == null) return false
        val ca = Calendar.getInstance().apply { time = a }
        val cb = Calendar.getInstance().apply { time = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) && ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
