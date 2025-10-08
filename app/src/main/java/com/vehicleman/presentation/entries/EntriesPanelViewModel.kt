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
    private val repository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EntriesPanelState(
        isSelectionMode = false, // ΔΕΝ ΧΡΕΙΑΖΕΤΑΙ ΠΛΕΟΝ
        selectedVehicleIds = emptySet() // ΔΕΝ ΧΡΕΙΑΖΕΤΑΙ ΠΛΕΟΝ
    ))
    val state: StateFlow<EntriesPanelState> = _state

    init {
        observeVehicles()
    }

    private fun observeVehicles() {
        viewModelScope.launch {
            repository.getAllVehicles()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = "Αποτυχία φόρτωσης οχημάτων: ${e.message}") }
                }
                .collectLatest { vehicles ->
                    _state.update { currentState ->
                        val displayItems = vehicles.map { vehicle ->
                            VehicleDisplayItem(
                                id = vehicle.id,
                                name = vehicle.name,
                                makeModel = "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                                licensePlate = vehicle.licensePlate,
                                isSelected = false // ΔΕΝ ΧΡΕΙΑΖΕΤΑΙ ΠΛΕΟΝ
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
            EntriesPanelEvent.AddNewVehicleClicked -> { /* Χειρισμός πλοήγησης στο HomeScreen */ }

            is EntriesPanelEvent.DeleteVehicleById -> {
                viewModelScope.launch {
                    try {
                        repository.deleteVehicleById(event.vehicleId)
                        // Η λίστα θα ενημερωθεί αυτόματα μέσω του Flow
                    } catch (e: Exception) {
                        // Εμφάνιση σφάλματος στο UI αν χρειαστεί
                        _state.update { it.copy(error = "Αδυναμία διαγραφής: ${e.message}") }
                    }
                }
            }
            // Αφαιρούμε τους παλιούς χειρισμούς συμβάντων
        }
    }
}