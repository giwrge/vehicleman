package com.vehicleman.domain.use_case.auto_reminder

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.RecordRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * Δημιουργεί αυτόματα υπενθυμίσεις για:
 * - Αλλαγή λαδιών (km/date)
 * - Αλλαγή ελαστικών (km/date)
 * - Ασφάλεια
 * - Τέλη κυκλοφορίας
 * - ΚΤΕΟ (κάθε 2 χρόνια)
 *
 * Δεν δημιουργεί διπλές υπενθυμίσεις.
 * Σώζει αυτόματα στη βάση.
 */
class GenerateAutoRemindersForVehicleUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle,
        existingRecords: List<Record>
    ): List<Record> {

        val newReminders = mutableListOf<Record>()

        // ---------------------------------------------------------------------
        // 1. Αλλαγή λαδιών — με βάση KM
        // ---------------------------------------------------------------------
        if (vehicle.oilChangeKm > 0L) {
            val nextCycle = vehicle.currentOdometer / vehicle.oilChangeKm
            val dueKm = (nextCycle + 1) * vehicle.oilChangeKm

            val exists = existingRecords.any {
                it.isReminder &&
                        it.recordType == RecordType.REMINDER &&
                        it.title.contains("Αλλαγή λαδιών", ignoreCase = true) &&
                        (it.reminderOdometer ?: -1) == dueKm.toInt()
            }

            if (!exists) {
                newReminders += buildKmReminder(
                    vehicle = vehicle,
                    title = "Αλλαγή λαδιών",
                    dueKm = dueKm
                )
            }
        }

        // ---------------------------------------------------------------------
        // 2. Αλλαγή λαδιών — με βάση Ημερομηνία
        // ---------------------------------------------------------------------
        if (vehicle.oilChangeDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.oilChangeDate.toInt())

            val exists = existingRecords.any {
                it.isReminder &&
                        it.title.contains("Αλλαγή λαδιών", ignoreCase = true) &&
                        isSameDay(it.reminderDate, dueDate)
            }

            if (!exists) {
                newReminders += buildDateReminder(
                    vehicle = vehicle,
                    title = "Αλλαγή λαδιών",
                    dueDate = dueDate
                )
            }
        }

        // ---------------------------------------------------------------------
        // 3. Αλλαγή ελαστικών — με βάση KM
        // ---------------------------------------------------------------------
        if (vehicle.tiresChangeKm > 0L) {
            val nextCycle = vehicle.currentOdometer / vehicle.tiresChangeKm
            val dueKm = (nextCycle + 1) * vehicle.tiresChangeKm

            val exists = existingRecords.any {
                it.isReminder &&
                        it.title.contains("Αλλαγή ελαστικών", ignoreCase = true) &&
                        (it.reminderOdometer ?: -1) == dueKm.toInt()
            }

            if (!exists) {
                newReminders += buildKmReminder(
                    vehicle = vehicle,
                    title = "Αλλαγή ελαστικών",
                    dueKm = dueKm
                )
            }
        }

        // ---------------------------------------------------------------------
        // 4. Αλλαγή ελαστικών — με βάση Ημερομηνία
        // ---------------------------------------------------------------------
        if (vehicle.tiresChangeDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.tiresChangeDate.toInt())

            val exists = existingRecords.any {
                it.isReminder &&
                        it.title.contains("Αλλαγή ελαστικών", ignoreCase = true) &&
                        isSameDay(it.reminderDate, dueDate)
            }

            if (!exists) {
                newReminders += buildDateReminder(
                    vehicle = vehicle,
                    title = "Αλλαγή ελαστικών",
                    dueDate = dueDate
                )
            }
        }

        // ---------------------------------------------------------------------
        // 5. Ασφάλεια (insuranceExpiryDate σε ημέρες από registrationDate)
        // ---------------------------------------------------------------------
        if (vehicle.insuranceExpiryDate > 0L) {
            val dueDate = addDays(vehicle.registrationDate, vehicle.insuranceExpiryDate.toInt())

            val exists = existingRecords.any {
                it.isReminder &&
                        it.title.contains("Ασφάλεια", ignoreCase = true) &&
                        isSameDay(it.reminderDate, dueDate)
            }

            if (!exists) {
                newReminders += buildDateReminder(
                    vehicle = vehicle,
                    title = "Ασφάλεια",
                    dueDate = dueDate
                )
            }
        }

        // ---------------------------------------------------------------------
        // 6. Τέλη κυκλοφορίας — πάντα 27/12
        // ---------------------------------------------------------------------
        val roadTaxDate = fixedDayMonth(27, Calendar.DECEMBER)

        val existsRoadTax = existingRecords.any {
            it.isReminder &&
                    it.title.contains("Τέλη κυκλοφορίας", ignoreCase = true) &&
                    isSameDay(it.reminderDate, roadTaxDate)
        }

        if (!existsRoadTax) {
            newReminders += buildDateReminder(
                vehicle = vehicle,
                title = "Τέλη κυκλοφορίας",
                dueDate = roadTaxDate
            )
        }

        // ---------------------------------------------------------------------
        // 7. ΚΤΕΟ — κάθε 2 χρόνια
        // ---------------------------------------------------------------------
        val kteoDueDate = addYears(vehicle.registrationDate, 2)

        val existsKteo = existingRecords.any {
            it.isReminder &&
                    it.title.contains("ΚΤΕΟ", ignoreCase = true) &&
                    isSameDay(it.reminderDate, kteoDueDate)
        }

        if (!existsKteo) {
            newReminders += buildDateReminder(
                vehicle = vehicle,
                title = "ΚΤΕΟ",
                dueDate = kteoDueDate
            )
        }

        // ---------------------------------------------------------------------
        // SAVE ALL NEW REMINDERS
        // ---------------------------------------------------------------------
        for (r in newReminders) {
            recordRepository.saveRecord(r)
        }

        return newReminders
    }

    // ============================================================================
    // HELPERS
    // ============================================================================

    private fun buildKmReminder(
        vehicle: Vehicle,
        title: String,
        dueKm: Long
    ): Record {
        return Record(
            id = java.util.UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            recordType = RecordType.REMINDER,
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
            isCompleted = false
        )
    }

    private fun buildDateReminder(
        vehicle: Vehicle,
        title: String,
        dueDate: Date
    ): Record {
        return Record(
            id = java.util.UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            recordType = RecordType.REMINDER,
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
            isCompleted = false
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
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
                ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
