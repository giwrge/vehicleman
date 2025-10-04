package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
     * StateFlow που περιέχει τη λίστα των οχημάτων (Domain Model: List<Vehicle>).
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
        val currentVehicles = repository.getAllVehicles().stateIn(viewModelScope).value

        if (currentVehicles.isEmpty()) {
            val initialVehicle = Vehicle(
                id = UUID.randomUUID().toString(),
                name = "VW Golf GTi",
                licensePlate = "ΙΟΝ-7700",
                year = 2018, // ΔΙΟΡΘΩΣΗ ΤΥΠΟΥ: Προσθήκη L (Long literal)
                make = "Volkswagen",
                model = "Golf GTi",
                fuelType = "Βενζίνη",
                initialOdometer = 85000, // ΔΙΟΡΘΩΣΗ ΤΥΠΟΥ: Προσθήκη L (Long literal)
                // ΔΙΟΡΘΩΣΗ ΤΥΠΟΥ: Χρησιμοποιούμε Long timestamp αντί για String
                registrationDate = System.currentTimeMillis()
            )
            repository.saveVehicle(initialVehicle)
        }
    }

    /**
     * Διαγράφει το όχημα από τη βάση. Δέχεται Vehicle (Domain Model).
     */
    fun deleteVehicle(vehicle: Vehicle) = viewModelScope.launch {
        repository.deleteVehicle(vehicle)
    }
}
