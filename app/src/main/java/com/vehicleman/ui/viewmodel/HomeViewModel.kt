package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first // ΝΕΟ IMPORT
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
     * StateFlow που περιέχει τη λίστα των οχημάτων (Domain Model: List<Vehicle>).
     * ΣΗΜΕΙΩΣΗ: Όπως συζητήσαμε, αυτό θα έπρεπε ιδανικά να είναι HomeUiState.
     */
    val vehicleListState: StateFlow<List<Vehicle>> =
        repository.getAllVehicles()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        insertInitialVehicle()
    }

    /**
     * Εισάγει ένα δοκιμαστικό όχημα αν δεν υπάρχουν ήδη οχήματα στη βάση.
     */
    private fun insertInitialVehicle() = viewModelScope.launch {
        // ΒΕΛΤΙΩΣΗ: Χρήση first() για ανάγνωση της πρώτης τιμής.
        val currentVehicles = repository.getAllVehicles().first()

        if (currentVehicles.isEmpty()) {
            val initialVehicle = Vehicle(
                id = UUID.randomUUID().toString(),
                name = "VW Golf GTi",
                licensePlate = "ΙΟΝ-7700",
                year = 2018,
                make = "Volkswagen",
                model = "Golf GTi",
                fuelType = "Βενζίνη",
                initialOdometer = 85000,
                registrationDate = System.currentTimeMillis()
            )
            // ΔΙΟΡΘΩΣΗ: Χρήση insertVehicle αντί για saveVehicle.
            repository.insertVehicle(initialVehicle)
        }
    }

    /**
     * Διαγράφει το όχημα από τη βάση. Δέχεται Vehicle (Domain Model).
     */
    fun deleteVehicle(vehicle: Vehicle) = viewModelScope.launch {
        repository.deleteVehicle(vehicle)
    }
}