// app/src/main/java/com/vehicleman/presentation/entries/EntriesPanelViewModel.kt (EntryListViewModel Class)
package com.vehicleman.presentation.entries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.MaintenanceRecord // ΔΙΟΡΘΩΣΗ: Χρησιμοποιούμε MaintenanceRecord
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.MaintenanceRepository // ΥΠΟΘΕΣΗ: Χρησιμοποιούμε MaintenanceRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.Calendar
import javax.inject.Inject

// Υπόθεση: Το EntryListState και το Airflow Model υπάρχουν

@HiltViewModel
class EntryListViewModel @Inject constructor(
    // ΔΙΟΡΘΩΣΗ: Αλλάζουμε το repository για να ταιριάζει με το MaintenanceRecord
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = checkNotNull(savedStateHandle["vehicleId"])

    // ... (τα _state, state παραμένουν ίδια) ...

    private var currentVehicle: Vehicle? = null // Αποθηκεύουμε το όχημα

    init {
        loadVehicleDetails()
        collectMaintenanceRecords() // ΔΙΟΡΘΩΣΗ: Νέα μέθοδος για συλλογή Maintenance Records
    }

    // Φόρτωση του οχήματος και των προσαρμοσμένων ρυθμίσεων
    private fun loadVehicleDetails() {
        viewModelScope.launch {
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                vehicle?.let {
                    currentVehicle = it
                    _state.update { currentState -> currentState.copy(vehicleName = "${it.make} ${it.model}") }
                }
            } catch (e: Exception) {
                // ...
            }
        }
    }

    private fun collectMaintenanceRecords() {
        // ΥΠΟΘΕΣΗ: Η συνάρτηση repository επιστρέφει Flow<List<MaintenanceRecord>>
        maintenanceRepository.getRecordsForVehicle(vehicleId)
            .onEach { allRecords ->
                // ΥΠΟΘΕΣΗ: Η τελευταία ένδειξη χιλιομετρητή είναι το odometer του πιο πρόσφατου record
                val latestOdometer = allRecords.firstOrNull()?.odometer ?: 0

                // ΥΠΟΘΕΣΗ: Υπάρχει συνάρτηση φιλτραρίσματος που δέχεται MaintenanceRecord
                val filteredRecords = filterRecords(allRecords, _state.value.filterType)

                _state.update { currentState ->
                    currentState.copy(
                        entries = filteredRecords, // Εμφανίζουμε τα filtered records στο UI
                        airflow = currentVehicle?.let {
                            calculateAirflow(allRecords, latestOdometer, it) // Υπολογισμός Airflow
                        },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // --- AIRFLOW LOGIC (ΤΕΛΙΚΗ ΕΝΗΜΕΡΩΣΗ) ---
    // Δέχεται MaintenanceRecord αντί για Entry
    private fun calculateAirflow(records: List<MaintenanceRecord>, latestOdometer: Int, vehicle: Vehicle): Airflow {

        // ΧΡΗΣΗ: Τα προσαρμοσμένα διαστήματα από το Vehicle Model
        val intervalKm = vehicle.oilChangeIntervalKm
        val intervalDays = vehicle.oilChangeIntervalDays

        // Βρίσκουμε την τελευταία αλλαγή λαδιών
        // ΥΠΟΘΕΣΗ: Το MaintenanceRecord έχει πεδίο 'type' ή 'description' που μπορούμε να φιλτράρουμε
        val oilChangeRecords = records.filter {
            it.type.contains("Oil Change", ignoreCase = true) ||
                    it.description.contains("λαδιών", ignoreCase = true)
        }
            .sortedByDescending { it.date }

        val lastOilChange = oilChangeRecords.firstOrNull()

        // 1. Υπολογισμός με βάση τα χιλιόμετρα
        val lastOilOdometer = lastOilChange?.odometer ?: vehicle.initialOdometer
        val nextDueOdometer = lastOilOdometer + intervalKm
        val remainingKm = nextDueOdometer - latestOdometer

        // 2. Υπολογισμός με βάση το χρόνο
        val nextDueDate = lastOilChange?.date?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it
            calendar.add(Calendar.DAY_OF_YEAR, intervalDays)
            calendar.time
        }

        val today = Date().startOfDay()
        val remainingDays = nextDueDate?.let {
            val diff = it.time - today.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }

        // 3. Έλεγχος Καθυστέρησης (Overdue)
        val isOverdue = (remainingKm <= 0 || (remainingDays != null && remainingDays < 0))

        return Airflow(
            vehicleId = vehicle.id,
            maintenanceType = "Επόμενη Αλλαγή Λαδιών",
            dueByDate = nextDueDate,
            dueByOdometer = nextDueOdometer,
            isOverdue = isOverdue,
            remainingDays = remainingDays?.coerceAtLeast(0),
            remainingKilometers = remainingKm.coerceAtLeast(0)
        )
    }

    // Υπόθεση: helper function
    private fun Date.startOfDay(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    // ... (Υπόλοιπες συναρτήσεις) ...
    // Placeholder για την filterRecords για να μην βγάζει σφάλμα ο compiler
    private fun filterRecords(records: List<MaintenanceRecord>, filterType: String): List<MaintenanceRecord> {
        // ΥΠΟΘΕΣΗ: Εδώ υπάρχει η λογική φιλτραρίσματος
        return records
    }
}