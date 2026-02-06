package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.auto_reminder.GenerateAutoRemindersForVehicleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Επιστρέφει στη RecordScreen ΟΛΑ όσα χρειάζεται:
 * - Λίστα οχημάτων
 * - Επιλεγμένο όχημα
 * - Όλες τις εγγραφές χρονολογικά
 * - Αυτόματα reminders που δημιουργούνται δυναμικά (ΑΝ είναι ενεργοποιημένα)
 */
class GetRecordScreenStateUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val recordRepository: RecordRepository,
    private val generateAutoRemindersForVehicleUseCase: GenerateAutoRemindersForVehicleUseCase,
    private val userPreferencesRepository: UserPreferencesRepository // <-- ΝΕΑ ΠΡΟΣΘΗΚΗ
) {

    operator fun invoke(selectedVehicleId: String): Flow<RecordScreenStateResult> {

        return combine(
            vehicleRepository.getAllVehicles(),
            recordRepository.getAllRecords()
        ) { vehicles, allRecords ->

            val selectedVehicle: Vehicle? =
                vehicles.firstOrNull { it.id == selectedVehicleId }

            val recordsForVehicle: List<Record> =
                allRecords.filter { it.vehicleId == selectedVehicleId }

            if (selectedVehicle == null) {
                return@combine RecordScreenStateResult(
                    vehicles = vehicles,
                    selectedVehicleId = selectedVehicleId,
                    timelineItems = emptyList()
                )
            }

            // ** Η ΑΛΛΑΓΗ ΕΙΝΑΙ ΕΔΩ **
            val showAutoReminders = userPreferencesRepository.showAutoReminders.first()
            val newReminders = if (showAutoReminders) {
                generateAutoRemindersForVehicleUseCase(
                    vehicle = selectedVehicle,
                    existingRecords = recordsForVehicle
                )
            } else {
                emptyList()
            }

            // 🔄 Τελική λίστα προς timeline
            val finalTimeline =
                (recordsForVehicle + newReminders)
                    .sortedByDescending { record ->
                        // Τα reminders ταξινομούνται με βάση reminderDate (αν έχει), αλλιώς date
                        record.reminderDate ?: record.date
                    }

            RecordScreenStateResult(
                vehicles = vehicles,
                selectedVehicleId = selectedVehicleId,
                timelineItems = finalTimeline
            )
        }
    }
}

data class RecordScreenStateResult(
    val vehicles: List<Vehicle>,
    val selectedVehicleId: String?,
    val timelineItems: List<Record>
)
