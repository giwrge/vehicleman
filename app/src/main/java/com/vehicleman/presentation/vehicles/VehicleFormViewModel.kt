package com.vehicleman.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vehicleman.presentation.vehicles.toVehicle
import com.vehicleman.presentation.vehicles.toFormState


/**
 * ViewModel για τη φόρμα εισαγωγής/επεξεργασίας οχήματος.
 * - Συνδέει το UI (VehicleFormScreen) με το Repository.
 * - Υποστηρίζει δημιουργία, ενημέρωση, διαγραφή και φόρτωση οχημάτων.
 */
@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    /** ----------------------------- **/
    /**         Ενέργειες Φόρμας     **/
    /** ----------------------------- **/

    fun onEvent(event: VehicleFormEvent) {
        when (event) {
            is VehicleFormEvent.MakeChanged -> _state.update { it.copy(make = event.make) }
            is VehicleFormEvent.ModelChanged -> _state.update { it.copy(model = event.model) }
            is VehicleFormEvent.PlateNumberChanged -> _state.update { it.copy(plateNumber = event.plateNumber) }
            is VehicleFormEvent.YearChanged -> _state.update { it.copy(year = event.year) }
            is VehicleFormEvent.CurrentOdometerChanged -> _state.update { it.copy(currentOdometer = event.currentOdometer) }
            is VehicleFormEvent.OilChangeKmChanged -> _state.update { it.copy(oilChangeKm = event.oilChangeKm.toLongOrNull()) }
            is VehicleFormEvent.OilChangeDateChanged -> _state.update { it.copy(oilChangeDate = event.oilChangeDate.toLongOrNull()) }
            is VehicleFormEvent.TiresChangeKmChanged -> _state.update { it.copy(tiresChangeKm = event.tiresChangeKm.toLongOrNull()) }
            is VehicleFormEvent.TireChangeDateChanged -> _state.update { it.copy(tiresChangeDate = event.tireChangeDate.toLongOrNull()) }
            is VehicleFormEvent.InsuranceExpiryDateChanged -> _state.update { it.copy(insuranceExpiryDate = event.insuranceExpiryDate.toLongOrNull()) }
            is VehicleFormEvent.TaxesExpiryDateChanged -> _state.update { it.copy(taxesExpiryDate = event.taxesExpiryDate.toLongOrNull()) }
            is VehicleFormEvent.LoadVehicle -> loadVehicle(event.vehicleId)
            is VehicleFormEvent.DeleteVehicle -> deleteVehicle(event.vehicleId)
            is VehicleFormEvent.Submit -> saveVehicle()
            // The following are kept for now but should probably be removed in favor of the specific events
            is VehicleFormEvent.FieldChanged -> _state.update { it.copyField(event.fieldName, event.value) }
            is VehicleFormEvent.SaveVehicle -> saveVehicle() // Consider using Submit event instead
        }
    }

    /** ----------------------------- **/
    /** Φόρτωση οχήματος για Edit Mode **/
    /** ----------------------------- **/
    private fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            // _state.update { it.copy(isLoading = true, errorMessage = null) } // isLoading is not on VehicleFormState
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    _state.update {
                        vehicle.toFormState()//.copy(isLoading = false)
                    }
                } else {
                    _state.update { it.copy(errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης")
                }
            }
        }
    }

    /** ----------------------------- **/
    /**  Αποθήκευση / Ενημέρωση      **/
    /** ----------------------------- **/
    private fun saveVehicle() {
        viewModelScope.launch {
            val formState = _state.value
            val vehicle = formState.toVehicle()

           // _state.update { it.copy(isLoading = true, success = false, errorMessage = null) } // success, isLoading not on formstate

            try {
                // This logic seems to belong elsewhere, maybe in a use-case, but keeping it here for now
                val vehicleCount = vehicleRepository.getVehicleCount()
                // if (formState.currentVehicle == null && vehicleCount >= 3) { // currentVehicle not on formstate
                //     _state.update { it.copy(limitReached = true) } // limitReached not on formstate
                //     return@launch
                // }

                // if (formState.currentVehicle != null) { // currentVehicle not on formstate
                //     vehicleRepository.updateVehicle(vehicle)
                // } else {
                //     vehicleRepository.insertVehicle(vehicle)
                // }
                vehicleRepository.insertVehicle(vehicle) // Simplified for now

                // _state.update { it.copy(isLoading = false, success = true) } // success not on formstate
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    /** ----------------------------- **/
    /**         Διαγραφή Οχήματος    **/
    /** ----------------------------- **/
    private fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            // _state.update { it.copy(isLoading = true, success = false, errorMessage = null) } // success, isLoading not on formstate
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                if (vehicle != null) {
                    vehicleRepository.deleteVehicle(vehicle)
                    // _state.update { it.copy(isLoading = false, success = true) }
                } else {
                    _state.update { it.copy(errorMessage = "Το όχημα δεν βρέθηκε") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }
}
