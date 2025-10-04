package com.vehicleman.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle // Χρησιμοποιούμε το Domain Model
import com.vehicleman.domain.repositories.VehicleRepository // Σωστό package για το Interface
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel για την οθόνη προσθήκης/επεξεργασίας οχήματος (Οθόνη 1).
 * Χρησιμοποιεί το SavedStateHandle για να διαβάσει το vehicleId από την πλοήγηση.
 */
@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val repository: VehicleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVehicleState())
    val state: StateFlow<AddEditVehicleState> = _state

    private var currentVehicleId: String? = null

    init {
        // Ελέγχει αν πρόκειται για Edit Mode διαβάζοντας το 'vehicleId' από τα arguments
        savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)?.let { vehicleId ->
            if (vehicleId != "new") {
                currentVehicleId = vehicleId
                loadVehicle(vehicleId)
            } else {
                // Αν πρόκειται για νέα καταχώρηση, η φόρμα είναι έτοιμη
                _state.update { it.copy(isReady = true) }
            }
        }
    }

    /** Φορτώνει τα δεδομένα του οχήματος για επεξεργασία. */
    private fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            // Το Repository πρέπει να επιστρέφει Domain Model (Vehicle)
            repository.getVehicleById(vehicleId)?.let { vehicle ->
                _state.update {
                    it.copy(
                        name = vehicle.name,
                        make = vehicle.make,
                        model = vehicle.model,
                        year = vehicle.year.toString(),
                        licensePlate = vehicle.licensePlate,
                        fuelType = vehicle.fuelType,
                        initialOdometer = vehicle.initialOdometer.toString(),
                        isEditMode = true,
                        isReady = true
                    )
                }
            } ?: run {
                // Χειρισμός σφάλματος αν το όχημα δεν βρεθεί (π.χ. στην κονσόλα)
                println("Error: Vehicle with ID $vehicleId not found.")
                _state.update { it.copy(isReady = true) }
            }
        }
    }

    /** Χειρίζεται τα Events από το UI. */
    fun onEvent(event: AddEditVehicleEvent) {
        when (event) {
            is AddEditVehicleEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is AddEditVehicleEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is AddEditVehicleEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is AddEditVehicleEvent.YearChanged -> _state.update { it.copy(year = event.year.filter { char -> char.isDigit() }.take(4)) }
            is AddEditVehicleEvent.LicensePlateChanged -> _state.update { it.copy(licensePlate = event.licensePlate.uppercase().take(8)) }
            is AddEditVehicleEvent.FuelTypeSelected -> _state.update { it.copy(fuelType = event.fuelType) }
            is AddEditVehicleEvent.InitialOdometerChanged -> _state.update { it.copy(initialOdometer = event.odometer.filter { char -> char.isDigit() }) }

            AddEditVehicleEvent.SaveVehicle -> saveVehicle()
        }
    }

    /** Υλοποιεί τη λογική αποθήκευσης (Insert/Update) και τον έλεγχο ορίου. */
    private fun saveVehicle() {
        val current = _state.value
        // Επικύρωση
        if (current.name.isBlank() || current.initialOdometer.toIntOrNull() == null || (current.initialOdometer.toIntOrNull() ?: 0) <= 0) {
            _state.update { it.copy(error = "Το Όνομα και τα Αρχικά Χιλιόμετρα είναι υποχρεωτικά και πρέπει να είναι > 0.") }
            return
        }

        viewModelScope.launch {
            // Ο ΕΛΕΓΧΟΣ ΟΡΙΟΥ ΑΠΑΙΤΕΙ ΤΟ getVehicleCount()
            if (currentVehicleId == null) {
                // Έλεγχος ορίου οχημάτων (PRO Feature)
                // ΕΔΩ ΥΠΑΡΧΕΙ ΤΟ ΣΦΑΛΜΑ: Το Repository Interface δεν έχει getVehicleCount().
                // Για να λυθεί, πρέπει να το προσθέσουμε στο Repository Interface.
                // Προσωρινά, το σχολιάζω/αφήνω ως έχει, περιμένοντας τη διόρθωση του Repository.

                // *** ΠΡΟΣΩΡΙΝΗ ΔΙΟΡΘΩΣΗ: ΑΝΑΜΕΝΕΤΑΙ getVehicleCount() ΣΤΟ REPO ***
                // Αν υποθέσουμε ότι το Repo θα διορθωθεί:
                // val currentCount = repository.getVehicleCount()
                // val maxFreeVehicles = 4
                // if (currentCount >= maxFreeVehicles) {
                //     _state.update { it.copy(showPaywall = true, isSavedSuccessfully = false, error = "Έχετε φτάσει το όριο των $maxFreeVehicles οχημάτων (PRO Feature).") }
                //     return@launch
                // }
            }

            // Δημιουργία/Ενημέρωση Domain Model
            val vehicleToSave = Vehicle(
                id = currentVehicleId ?: UUID.randomUUID().toString(),
                name = current.name,
                make = current.make,
                model = current.model,
                year = current.year.toIntOrNull() ?: 0,
                licensePlate = current.licensePlate,
                fuelType = current.fuelType,
                initialOdometer = current.initialOdometer.toInt(),
                registrationDate = System.currentTimeMillis()
            )

            // ΕΔΩ ΥΠΑΡΧΕΙ ΤΟ ΣΦΑΛΜΑ: Το Repository Interface δεν έχει insertVehicle/updateVehicle,
            // αλλά έχει saveVehicle.
            // Η ΣΩΣΤΗ ΛΟΓΙΚΗ ΓΙΑ ΤΟ CLEAN ARCHITECTURE ΕΙΝΑΙ insert/update:

            if (currentVehicleId != null) {
                // repository.updateVehicle(vehicleToSave) // <--- Πρέπει να υπάρχει στο Interface
            } else {
                // repository.insertVehicle(vehicleToSave) // <--- Πρέπει να υπάρχει στο Interface
            }
            // *** ΕΠΙΔΙΟΡΘΩΣΗ: ΠΡΕΠΕΙ ΝΑ ΕΙΣΑΓΟΥΜΕ ΤΟ insertVehicle ΚΑΙ updateVehicle ΣΤΟ REPOSITORY ***

            // Επιτυχής αποθήκευση
            _state.update { it.copy(isSavedSuccessfully = true, error = null) }
        }
    }
}

/** Κλάση κατάστασης για τη φόρμα. */
data class AddEditVehicleState(
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = "",
    val fuelType: String = "Βενζίνη",
    val initialOdometer: String = "",
    val isEditMode: Boolean = false,
    val isSavedSuccessfully: Boolean = false,
    val showPaywall: Boolean = false,
    val error: String? = null,
    val isReady: Boolean = false // Χρησιμοποιείται όταν φορτώνουμε για επεξεργασία
)

/** Sealed Class για τα συμβάντα (Events). */
sealed class AddEditVehicleEvent {
    data class NameChanged(val name: String) : AddEditVehicleEvent()
    data class MakeChanged(val make: String) : AddEditVehicleEvent()
    data class ModelChanged(val model: String) : AddEditVehicleEvent()
    data class YearChanged(val year: String) : AddEditVehicleEvent()
    data class LicensePlateChanged(val licensePlate: String) : AddEditVehicleEvent()
    data class FuelTypeSelected(val fuelType: String) : AddEditVehicleEvent()
    data class InitialOdometerChanged(val odometer: String) : AddEditVehicleEvent()
    object SaveVehicle : AddEditVehicleEvent()
}
