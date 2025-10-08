package com.vehicleman.presentation.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Υπόθεση: Το domain.model.Vehicle δεν είναι απαραίτητο να γίνει import εδώ αν δεν χρησιμοποιείται
// απευθείας στο State, αλλά το Repository το χρησιμοποιεί.

/**
 * State class for the Entries Panel (Home Screen).
 */
data class EntriesPanelState(
    val vehicles: List<VehicleDisplayItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSelectionMode: Boolean = false,
    val selectedVehicleIds: Set<String> = emptySet()
)

/** Item used for display in the Composable, includes selection status. */
data class VehicleDisplayItem(
    val id: String,
    val name: String,
    val makeModel: String, // π.χ., "Toyota Corolla (2020)"
    val licensePlate: String,
    val isSelected: Boolean = false
)

/**
 * ViewModel for the main vehicle list (Entries Panel).
 */
@HiltViewModel
class EntriesPanelViewModel @Inject constructor(
    private val repository: VehicleRepository // Τώρα το Hilt μπορεί να κάνει inject αυτό
) : ViewModel() {

    private val _state = MutableStateFlow(EntriesPanelState())
    val state: StateFlow<EntriesPanelState> = _state

    init {
        observeVehicles()
    }

    private fun observeVehicles() {
        viewModelScope.launch {
            repository.getAllVehicles()
                .catch { e ->
                    // Χειρισμός σφάλματος φόρτωσης
                    _state.update { it.copy(isLoading = false, error = "Αποτυχία φόρτωσης οχημάτων: ${e.message}") }
                }
                .collectLatest { vehicles ->
                    _state.update { currentState ->
                        val displayItems = vehicles.map { vehicle ->
                            VehicleDisplayItem(
                                id = vehicle.id,
                                name = vehicle.name,
                                // Κατασκευή του string makeModel από το domain model
                                makeModel = "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                                licensePlate = vehicle.licensePlate,
                                isSelected = currentState.selectedVehicleIds.contains(vehicle.id)
                            )
                        }
                        currentState.copy(
                            vehicles = displayItems,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /** Handles UI Events from the Entries Panel Composable. */
    fun onEvent(event: EntriesPanelEvent) {
        when (event) {
            EntriesPanelEvent.AddNewVehicleClicked -> { /* Handled in HomeScreen for navigation */ }
            is EntriesPanelEvent.VehicleClicked -> { /* Handled in HomeScreen for navigation */ }

            EntriesPanelEvent.ToggleSelectionMode -> {
                _state.update {
                    it.copy(
                        isSelectionMode = !it.isSelectionMode,
                        // Όταν βγαίνουμε από το selection mode, καθαρίζουμε τις επιλογές
                        selectedVehicleIds = if (!it.isSelectionMode) it.selectedVehicleIds else emptySet()
                    )
                }
            }

            is EntriesPanelEvent.ToggleVehicleSelection -> {
                val vehicleId = event.vehicleId
                _state.update { currentState ->
                    val newSelection = if (currentState.selectedVehicleIds.contains(vehicleId)) {
                        currentState.selectedVehicleIds - vehicleId
                    } else {
                        currentState.selectedVehicleIds + vehicleId
                    }
                    currentState.copy(
                        selectedVehicleIds = newSelection,
                        // Μπαίνουμε σε selection mode αν υπάρχει τουλάχιστον ένα επιλεγμένο
                        isSelectionMode = newSelection.isNotEmpty()
                    )
                }
            }

            EntriesPanelEvent.DeleteSelectedVehicles -> {
                val idsToDelete = _state.value.selectedVehicleIds
                if (idsToDelete.isNotEmpty()) {
                    viewModelScope.launch {
                        // Καλούμε τη συνάρτηση διαγραφής στο Repository
                        repository.deleteVehiclesByIds(idsToDelete)

                        // Επαναφορά κατάστασης UI μετά τη διαγραφή
                        _state.update {
                            it.copy(
                                isSelectionMode = false,
                                selectedVehicleIds = emptySet()
                            )
                        }
                    }
                }
            }
        }
    }
}