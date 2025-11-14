package com.vehicleman.presentation.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.use_case.GetRecordScreenStateUseCase
import com.vehicleman.domain.use_case.RecordUseCases
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getRecordScreenStateUseCase: GetRecordScreenStateUseCase,
    private val recordUseCases: RecordUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RecordState(isLoading = true))
    val state: StateFlow<RecordState> = _state.asStateFlow()

    private val expandedIds = MutableStateFlow<Set<String>>(emptySet())

    val vehicleId: String? = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)

    init {
        vehicleId?.let { loadScreenState(it) }
    }

    fun onEvent(event: RecordEvent) {
        when (event) {
            is RecordEvent.VehicleSelected -> {
                loadScreenState(event.vehicleId)
            }
            is RecordEvent.Refresh -> {
                vehicleId?.let { loadScreenState(it) }
            }
            is RecordEvent.ToggleExpandRecord -> {
                toggleExpand(event.recordId)
            }
            is RecordEvent.MarkReminderCompleted -> {
                markReminderCompletedById(event.recordId)
            }
            is RecordEvent.NavigateToEdit -> {
                // χειρίζεται η UI (RecordScreen) με onNavigateToAddEditRecord
            }
            is RecordEvent.DeleteRecord -> {
                val recordToDelete = _state.value.timelineItems.firstOrNull { it.id == event.recordId }
                if (recordToDelete != null) {
                    deleteRecord(recordToDelete)
                }
            }
        }
    }

    /** Χρησιμοποιείται από το UI για swipe-to-delete + undo. */
    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            recordUseCases.deleteRecord(record)
            // To GetRecordScreenStateUseCase θα ξαναστείλει νέο state μετά το delete.
        }
    }

    /** Χρησιμοποιείται από το UI για UNDO (re-insert). */
    fun saveRecord(record: Record) {
        viewModelScope.launch {
            recordUseCases.saveRecord(record)
        }
    }

    /** Χρησιμοποίηση υπάρχοντος SaveRecord για ολοκλήρωση υπενθύμισης. */
    private fun markReminderCompletedById(recordId: String) {
        val currentRecord = _state.value.timelineItems.firstOrNull { it.id == recordId } ?: return
        val completed = currentRecord.copy(isCompleted = true)
        saveRecord(completed)
    }

    private fun toggleExpand(recordId: String) {
        val current = expandedIds.value
        expandedIds.value =
            if (current.contains(recordId)) current - recordId else current + recordId
    }

    private fun loadScreenState(vehicleId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        getRecordScreenStateUseCase(vehicleId)
            .onEach { recordStateFromUseCase ->
                // Ο use case σου ήδη επιστρέφει RecordState με: vehicles, selectedVehicleId, timelineItems
                val timeline = recordStateFromUseCase.timelineItems
                val vehicles = recordStateFromUseCase.vehicles
                val selected = recordStateFromUseCase.selectedVehicleId ?: vehicleId

                val uiState = computeUiStateFromTimeline(
                    vehicles = vehicles,
                    selectedVehicleId = selected,
                    timeline = timeline,
                    isLoading = false,
                    error = null
                )
                _state.value = uiState
            }
            .catch { ex ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Άγνωστο σφάλμα"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Εδώ εφαρμόζουμε τη λογική:
     * - Expenses: non-reminders, sort DESC by date (πιο πρόσφατα πρώτα, πάνε προς τα κάτω)
     * - Reminders: reminders, sort ASC by reminderDate (μελλοντικά προς τα πάνω)
     * - latestUpcomingReminder: πιο κοντινή μελλοντική υπενθύμιση (sticky)
     */
    private fun computeUiStateFromTimeline(
        vehicles: List<Vehicle>,
        selectedVehicleId: String?,
        timeline: List<Record>,
        isLoading: Boolean,
        error: String?
    ): RecordState {
        val expenseRecords = timeline
            .filter { !it.isReminder }
            .sortedByDescending { it.date }

        val reminderRecords = timeline
            .filter { it.isReminder }
            .sortedWith(
                compareBy<Record> { it.reminderDate ?: Date(Long.MAX_VALUE) }
                    .thenBy { it.reminderOdometer ?: Int.MAX_VALUE }
            )

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
