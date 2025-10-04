package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel για την κύρια οθόνη (Home Screen).
 * Διαχειρίζεται την ανάκτηση της λίστας οχημάτων και την αρχική εισαγωγή δεδομένων.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    /**
     * StateFlow που περιέχει τη λίστα των οχημάτων.
     * Το UI θα κάνει collect αυτό το flow για ενημερώσεις σε πραγματικό χρόνο.
     */
    val vehicleListState: StateFlow<List<VehicleEntity>> =
        repository.getAllVehicles()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        // Εισάγει ένα δοκιμαστικό όχημα κατά την πρώτη εκκίνηση του ViewModel
        insertInitialVehicle()
    }

    /**
     * Εισάγει ένα δοκιμαστικό όχημα αν δεν υπάρχουν ήδη οχήματα στη βάση.
     * Αυτό βοηθά στην επιβεβαίωση ότι ολόκληρο το Data Stack λειτουργεί.
     */
    private fun insertInitialVehicle() = viewModelScope.launch {
        // Ελέγχει αν υπάρχουν ήδη δεδομένα
        if (repository.getAllVehicles().map { it.isEmpty() }.stateIn(viewModelScope).value) {
            val initialVehicle = VehicleEntity(
                id = UUID.randomUUID().toString(),
                name = "VW Golf GTi",
                licensePlate = "ΙΟΝ-7700",
                year = 2018, // Changed to Int
                make = "Volkswagen",
                model = "Golf GTi",
                fuelType = "Βενζίνη",
                initialOdometer = 85000 // Changed to Int
            )
            repository.saveVehicle(initialVehicle)
        }
    }

    /**
     * Διαγράφει το όχημα από τη βάση.
     */
    fun deleteVehicle(vehicle: VehicleEntity) = viewModelScope.launch {
        repository.deleteVehicle(vehicle)
    }

    /**
     * Διαγράφει πολλαπλά οχήματα με βάση τα IDs τους (χρησιμοποιείται από το EntriesPanel).
     */
    fun deleteVehiclesByIds(vehicleIds: Set<String>) = viewModelScope.launch {
        repository.deleteVehiclesByIds(vehicleIds)
    }
}
