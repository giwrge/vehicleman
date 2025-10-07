package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.update
/**
 * ViewModel for the Entries Panel.
 * Handles the list of vehicles, selection logic (long tap),
 * and delete/modify functions.
 */
@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    // State of the vehicle list
    private val _state = MutableStateFlow(EntriesState())
    val state: StateFlow<EntriesState> = _state.asStateFlow()

    init {
        // Monitoring all vehicles from the database
        repository.getAllVehicles()
            .onEach { vehicles ->
                _state.update { it.copy(vehicles = vehicles) }            }
            .launchIn(viewModelScope)
    }

    /**
     * Handles events (Events) coming from the UI (e.g., Long Tap, Delete).
     */
    fun onEvent(event: EntriesEvent) {
        when (event) {
            is EntriesEvent.ToggleVehicleSelection -> {
                val selectedId = event.vehicleId

                _state.update { currentState -> // Χρήση του update{}
                    val currentlySelected = currentState.selectedVehicleIds.toMutableSet()

                    if (currentlySelected.contains(selectedId)) {
                        currentlySelected.remove(selectedId)
                    } else {
                        currentlySelected.add(selectedId)
                    }

                    currentState.copy(
                        selectedVehicleIds = currentlySelected,
                        // If no vehicle is selected anymore, exit Selection Mode
                        isSelectionMode = currentlySelected.isNotEmpty()
                    )
                }
            }
            EntriesEvent.ExitSelectionMode -> {
                _state.update {
                    it.copy(
                        isSelectionMode = false,
                        selectedVehicleIds = emptySet()
                    )
                }
            }
            EntriesEvent.DeleteSelectedVehicles -> {
                viewModelScope.launch {
                    val idsToDelete = _state.value.selectedVehicleIds

                    if (idsToDelete.isNotEmpty()) {
                        // Χρησιμοποιούμε Set<String> όπως ορίζεται στο Repository
                        repository.deleteVehiclesByIds(idsToDelete)
                    }

                    // After deletion, exit Selection Mode
                    onEvent(EntriesEvent.ExitSelectionMode)
                }
            }
        }
    }
}

/**
 * State of the Entries Panel.
 * @param vehicles The list of vehicles.
 * @param isSelectionMode Whether the user is in selection mode.
 * @param selectedVehicleIds The IDs of the selected vehicles.
 */
data class EntriesState(
    val vehicles: List<Vehicle> = emptyList(),
    val isSelectionMode: Boolean = false,
    val selectedVehicleIds: Set<String> = emptySet()
)

/**
 * Events sent from the UI to the ViewModel.
 */
sealed class EntriesEvent {
    data class ToggleVehicleSelection(val vehicleId: String) : EntriesEvent()
    object ExitSelectionMode : EntriesEvent()
    object DeleteSelectedVehicles : EntriesEvent()
}
