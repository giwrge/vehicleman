package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.*
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
    private val fakeDataMarker = "Αυτόματη εγγραφή fake data"
    private val fakeReminderMarker = "Προγραμματισμένη υπενθύμιση (Fake Data)"

    suspend operator fun invoke(vehicleId: String? = null) {
        if (vehicleId != null) {
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            vehicle?.let { generateFakeDataForVehicle(it) }
        } else {
            val vehicles = vehicleRepository.getAllVehiclesList()
            if (vehicles.isEmpty()) return
            vehicles.forEach { vehicle ->
                generateFakeDataForVehicle(vehicle)
            }
        }
    }

    suspend fun deleteFakeData(vehicleId: String) {
        val records = recordRepository.getRecordsByVehicle(vehicleId)
        records.forEach { record ->
            if (record.description == fakeDataMarker || record.description == fakeReminderMarker) {
                recordRepository.deleteRecord(record)
            }
        }
    }

    private suspend fun generateFakeDataForVehicle(vehicle: Vehicle) {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        
        // 1. Ξεκινάμε από την 1η Ιανουαρίου του έτους του οχήματος
        val startYear = if (vehicle.year > 1900) vehicle.year else 2020
        calendar.set(startYear, 0, 1, 0, 0, 0)
        val startDate = calendar.time
        
        if (startDate.after(today)) return

        val targetOdometer = vehicle.currentOdometer
        val diffMillis = today.time - startDate.time
        val totalDays = (diffMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        
        // Υπολογισμός μέσων χιλιομέτρων ανά ημέρα για να φτάσουμε στο τωρινό οδόμετρο
        val avgKmPerDay = targetOdometer.toDouble() / totalDays
        
        var simulatedOdometer = 0.0
        var lastOilServiceKm = 0.0
        var lastOilServiceDate = startDate.time
        var lastGeneralServiceKm = 0.0
        var lastTiresRotationKm = 0.0
        var lastTiresReplacementKm = 0.0
        var lastTiresReplacementDate = startDate.time
        var distanceSinceLastFuelUp = 0.0

        // Βάση για Ασφάλεια και Τέλη (ετήσια επανάληψη)
        val insuranceBase = Calendar.getInstance().apply { 
            timeInMillis = if (vehicle.insuranceExpiryDate > 0) vehicle.insuranceExpiryDate else System.currentTimeMillis() 
        }
        val taxesBase = Calendar.getInstance().apply { 
            timeInMillis = if (vehicle.taxesExpiryDate > 0) vehicle.taxesExpiryDate else System.currentTimeMillis() 
        }

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = startDate

        // Παράμετροι προσομοίωσης κατανάλωσης
        val fuelEfficiency = when {
            vehicle.fuelTypes.contains("electric") -> 5.5 // km/kWh
            vehicle.fuelTypes.contains("diesel") || vehicle.fuelTypes.contains("b7") -> 16.0 // km/L
            else -> 12.5 // km/L (περίπου 8L/100km)
        }
        val tankCapacity = 40.0

        // Προσομοίωση μέρα με τη μέρα
        while (tempCalendar.time.before(today)) {
            val dayOfYear = tempCalendar.get(Calendar.DAY_OF_YEAR)
            val currentDate = tempCalendar.time
            
            // Αύξηση χιλιομέτρων
            val dailyKm = avgKmPerDay * (0.7 + Random.nextDouble(0.6)) // Διακύμανση στην οδήγηση
            simulatedOdometer = (simulatedOdometer + dailyKm).coerceAtMost(targetOdometer.toDouble())
            distanceSinceLastFuelUp += dailyKm

            // 1. Σταθερά Ετήσια Έξοδα (Ασφάλεια & Τέλη)
            if (dayOfYear == insuranceBase.get(Calendar.DAY_OF_YEAR)) {
                saveExpense(vehicle.id, "ΑΝΑΝΕΩΣΗ ΑΣΦΑΛΕΙΑΣ", RecordExpenseCategory.INSURANCE, currentDate, simulatedOdometer.toInt(), Random.nextDouble(180.0, 450.0))
            }
            if (dayOfYear == taxesBase.get(Calendar.DAY_OF_YEAR)) {
                saveExpense(vehicle.id, "ΤΕΛΗ ΚΥΚΛΟΦΟΡΙΑΣ", RecordExpenseCategory.TAXES, currentDate, simulatedOdometer.toInt(), Random.nextDouble(120.0, 280.0))
            }

            // 2. Λογική Καυσίμων
            if (distanceSinceLastFuelUp >= 450 || (distanceSinceLastFuelUp > 200 && Random.nextInt(20) == 0)) {
                val fuelType = vehicle.fuelTypes.randomOrNull() ?: "unleaded_95"
                val pricePerUnit = getFuelPrice(fuelType)
                val quantity = (distanceSinceLastFuelUp / fuelEfficiency).coerceAtMost(tankCapacity)
                
                val record = Record(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    recordType = RecordType.FUEL_UP,
                    category = RecordExpenseCategory.FUEL,
                    title = "ΑΝΕΦΟΔΙΑΣΜΟΣ ΚΑΥΣΙΜΟΥ",
                    description = fakeDataMarker,
                    date = currentDate,
                    odometer = simulatedOdometer.toInt(),
                    cost = quantity * pricePerUnit,
                    quantity = quantity,
                    pricePerUnit = pricePerUnit,
                    fuelType = fuelType,
                    isFullTank = quantity > 35.0,
                    isReminder = false,
                    reminderDate = null,
                    reminderOdometer = null,
                    isCompleted = true
                )
                recordRepository.saveRecord(record)
                distanceSinceLastFuelUp = 0.0
            }

            // 3. Λογική Service
            val kmSinceOil = simulatedOdometer - lastOilServiceKm
            val daysSinceOil = (currentDate.time - lastOilServiceDate) / (1000 * 60 * 60 * 24)
            val kmSinceGeneral = simulatedOdometer - lastGeneralServiceKm

            if (kmSinceGeneral >= 40000) {
                saveExpense(vehicle.id, "ΓΕΝΙΚΟ SERVICE", RecordExpenseCategory.SERVICE, currentDate, simulatedOdometer.toInt(), Random.nextDouble(350.0, 700.0))
                lastGeneralServiceKm = simulatedOdometer
                lastOilServiceKm = simulatedOdometer
                lastOilServiceDate = currentDate.time
                
                // Υπενθυμίσεις
                saveReminder(vehicle.id, "ΕΠΟΜΕΝΟ ΓΕΝΙΚΟ SERVICE", currentDate, today, simulatedOdometer.toInt() + 40000, addDays(currentDate, 730))
                saveReminder(vehicle.id, "ΣΕΡΒΙΣ ΛΑΔΙΑ & ΦΙΛΤΡΑ", currentDate, today, simulatedOdometer.toInt() + 7500, addDays(currentDate, 365))
            } else if (kmSinceOil >= 7500 || daysSinceOil >= 365) {
                saveExpense(vehicle.id, "SERVICE ΛΑΔΙΑ & ΦΙΛΤΡΑ", RecordExpenseCategory.SERVICE, currentDate, simulatedOdometer.toInt(), Random.nextDouble(70.0, 130.0))
                lastOilServiceKm = simulatedOdometer
                lastOilServiceDate = currentDate.time
                
                // Υπενθύμιση
                saveReminder(vehicle.id, "ΣΕΡΒΙΣ ΛΑΔΙΑ & ΦΙΛΤΡΑ", currentDate, today, simulatedOdometer.toInt() + 7500, addDays(currentDate, 365))
            }

            // 4. Λογική Ελαστικών
            val kmSinceReplacement = simulatedOdometer - lastTiresReplacementKm
            val daysSinceReplacement = (currentDate.time - lastTiresReplacementDate) / (1000 * 60 * 60 * 24)

            if (kmSinceReplacement >= 40000 || daysSinceReplacement >= 365 * 4) {
                saveExpense(vehicle.id, "ΑΓΟΡΑ ΝΕΩΝ ΕΛΑΣΤΙΚΩΝ", RecordExpenseCategory.TIRES, currentDate, simulatedOdometer.toInt(), Random.nextDouble(280.0, 550.0))
                lastTiresReplacementKm = simulatedOdometer
                lastTiresReplacementDate = currentDate.time
                lastTiresRotationKm = simulatedOdometer
                
                // 2 Υπενθυμίσεις
                saveReminder(vehicle.id, "ΑΛΛΑΓΗ ΕΛΑΣΤΙΚΩΝ ΜΠΡΟΣ ΠΙΣΩ", currentDate, today, simulatedOdometer.toInt() + 20000, addDays(currentDate, 730))
                saveReminder(vehicle.id, "ΑΓΟΡΑ ΝΕΩΝ ΕΛΑΣΤΙΚΩΝ", currentDate, today, simulatedOdometer.toInt() + 40000, addDays(currentDate, 1460))
            } else if (simulatedOdometer - lastTiresRotationKm >= 20000) {
                saveExpense(vehicle.id, "ΑΛΛΑΓΗ ΕΛΑΣΤΙΚΩΝ ΜΠΡΟΣ ΠΙΣΩ", RecordExpenseCategory.TIRES, currentDate, simulatedOdometer.toInt(), Random.nextDouble(30.0, 60.0))
                lastTiresRotationKm = simulatedOdometer
            }

            // 5. Τυχαίες επισκευές και ανταλλακτικά
            if (Random.nextInt(500) == 0) { // Περίπου μια φορά στα 1.5 χρόνια
                val repairTitles = listOf("ΑΛΛΑΓΗ ΜΠΑΤΑΡΙΑΣ", "ΤΑΚΑΚΙΑ ΦΡΕΝΩΝ", "ΜΑΚΤΡΑ ΥΑΛΟΚΑΘΑΡΙΣΤΗΡΩΝ", "ΛΑΜΠΕΣ ΦΩΤΩΝ", "ΑΝΤΛΙΑ ΝΕΡΟΥ")
                saveExpense(vehicle.id, repairTitles.random(), RecordExpenseCategory.REPAIRS, currentDate, simulatedOdometer.toInt(), Random.nextDouble(15.0, 250.0))
            }

            tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun getFuelPrice(fuelType: String): Double {
        return when (fuelType) {
            "unleaded_95" -> Random.nextDouble(1.799, 1.999)
            "unleaded_98" -> Random.nextDouble(1.899, 2.099)
            "unleaded_100" -> Random.nextDouble(2.099, 2.299)
            "electric" -> Random.nextDouble(0.097, 0.38)
            "diesel", "b7" -> Random.nextDouble(1.550, 1.750)
            "lpg", "autogas" -> Random.nextDouble(0.780, 0.950)
            "cng" -> Random.nextDouble(1.050, 1.250)
            else -> 1.850
        }
    }

    private suspend fun saveExpense(vehicleId: String, title: String, category: RecordExpenseCategory, date: Date, odometer: Int, cost: Double) {
        val record = Record(
            id = UUID.randomUUID().toString(),
            vehicleId = vehicleId,
            recordType = RecordType.EXPENSE,
            category = category,
            title = title,
            description = fakeDataMarker,
            date = date,
            odometer = odometer,
            cost = cost,
            quantity = null,
            pricePerUnit = null,
            isReminder = false,
            reminderDate = null,
            reminderOdometer = null,
            isCompleted = true
        )
        recordRepository.saveRecord(record)
    }

    private suspend fun saveReminder(vehicleId: String, title: String, createdDate: Date, today: Date, targetOdometer: Int, dueDate: Date) {
        val isCompleted = dueDate.before(today)
        
        val record = Record(
            id = UUID.randomUUID().toString(),
            vehicleId = vehicleId,
            recordType = RecordType.REMINDER,
            category = RecordExpenseCategory.OTHER,
            title = title,
            description = fakeReminderMarker,
            date = createdDate,
            odometer = (targetOdometer - 7500).coerceAtLeast(0), // Περίπου πότε δημιουργήθηκε
            cost = null,
            quantity = null,
            pricePerUnit = null,
            isReminder = true,
            reminderDate = dueDate,
            reminderOdometer = targetOdometer,
            isCompleted = isCompleted
        )
        recordRepository.saveRecord(record)
    }

    private fun addDays(date: Date, days: Int): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DAY_OF_YEAR, days)
        return c.time
    }
}
