package com.vehicleman.presentation.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.use_case.GetRecordScreenStateUseCase
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * RecordViewModel — πιο πλήρης/ασφαλής υλοποίηση που:
 * - Παρακολουθεί ένα use-case (που επιστρέφει RecordState-like data)
 * - Επεξεργάζεται τα raw timeline items σε expenses/reminders & latestUpcomingReminder
 *
 * Διατηρεί συμβατότητα με το υπάρχον GetRecordScreenStateUseCase (όπως στο παλιό ViewModel).
 * Αν προτιμάς άλλο use case, πες μου το όνομα/πακέτο και το αλλάζω.
 */
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getRecordScreenStateUseCase: GetRecordScreenStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Exposed state as StateFlow for easy Compose consumption
    private val _uiState = MutableStateFlow(RecordState(isLoading = true))
    val state: StateFlow<RecordState> = _uiState.asStateFlow()

    // track expanded cards (by id)
    private val expandedIds = MutableStateFlow<Set<String>>(emptySet())

    private val vehicleIdArg: String? = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)

    init {
        vehicleIdArg?.let { loadScreenState(it) }
    }

    fun onEvent(event: RecordEvent) {
        when (event) {
            is RecordEvent.VehicleSelected -> {
                loadScreenState(event.vehicleId)
            }
            is RecordEvent.Refresh -> {
                vehicleIdArg?.let { loadScreenState(it) }
            }
            is RecordEvent.ToggleExpandRecord -> {
                toggleExpand(event.recordId)
            }
            is RecordEvent.MarkReminderCompleted -> {
                // forward to use-case / repository: (not implemented here)
                // For now we optimistically update UI (if underlying use-case supports it replace with a call)
                markCompletedLocally(event.recordId)
            }
            is RecordEvent.DeleteRecord -> {
                // Delegate to use-case or repository (ask user if they want me to implement delete use-case)
            }
            is RecordEvent.NavigateToEdit -> {
                // navigation is handled in UI layer; ViewModel can issue effects if needed
            }
        }
    }

    private fun toggleExpand(recordId: String) {
        val current = expandedIds.value
        expandedIds.value = if (current.contains(recordId)) current - recordId else current + recordId
    }

    private fun markCompletedLocally(recordId: String) {
        // local update of the state for instant feedback (non-persistent)
        val current = _uiState.value
        val newTimeline = current.timelineItems.map { r ->
            if (r.id == recordId) r.copy(isCompleted = true) else r
        }
        // recompute split lists
        _uiState.value = computeUiStateFromTimeline(
            vehicles = current.vehicles,
            selectedVehicleId = current.selectedVehicleId,
            timeline = newTimeline,
            isLoading = false,
            error = null
        )
    }

    private fun loadScreenState(vehicleId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        getRecordScreenStateUseCase(vehicleId)
            .onEach { domainState ->
                // domainState presumed to contain vehicles + timelineRecords or similar.
                // We are defensive: accept a list of Records from the use case.
                val timeline = domainState.timelineItems
                val vehicles = domainState.vehicles
                val selected = domainState.selectedVehicleId ?: vehicleId
                val uiState = computeUiStateFromTimeline(vehicles, selected, timeline, isLoading = false, error = null)
                _uiState.value = uiState
            }
            .catch { ex ->
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Unknown error") }
            }
            .launchIn(viewModelScope)
    }

    private fun computeUiStateFromTimeline(
        vehicles: List<com.vehicleman.domain.model.Vehicle>,
        selectedVehicleId: String?,
        timeline: List<Record>,
        isLoading: Boolean,
        error: String?
    ): RecordState {
        // Expenses: not reminders (recordType == EXPENSE or FUEL_UP) sorted DESC by date
        val expenseRecords = timeline.filter { !it.isReminder }
            .sortedByDescending { it.date }

        // Reminders: records where isReminder == true sorted ASC by reminderDate (nulls last)
        val reminderRecords = timeline.filter { it.isReminder }
            .sortedWith(compareBy<Record> { it.reminderDate ?: Date(Long.MAX_VALUE) }
                .thenBy { it.reminderOdometer ?: Int.MAX_VALUE })

        // latest upcoming reminder: nearest future reminder by date (reminderDate > now) if any
        val now = Date()
        val latestUpcoming = reminderRecords.firstOrNull { it.reminderDate?.after(now) ?: false }

        return RecordState(
            vehicles = vehicles,
            selectedVehicleId = selectedVehicleId,
            timelineItems = timeline,
            expenseRecords = expenseRecords,
            reminderRecords = reminderRecords,
            latestUpcomingReminder = latestUpcoming,
            isLoading = isLoading,
            errorMessage = error
        )
    }
}
