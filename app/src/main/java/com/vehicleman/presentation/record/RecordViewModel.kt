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

    val vehicleId: String? = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)

    private var lastHiddenTimestamp: Long = 0L
    var savedScrollIndex: Int = 0
    var savedScrollOffset: Int = 0

    init {
        vehicleId?.let { loadScreenState(it) }
    }

    fun onEvent(event: RecordEvent) {
        when (event) {
            is RecordEvent.VehicleSelected -> loadScreenState(event.vehicleId)
            is RecordEvent.Refresh -> vehicleId?.let { loadScreenState(it) }
            is RecordEvent.ToggleExpandRecord -> { /* Not used in this simplified version */ }
            is RecordEvent.MarkReminderCompleted -> markReminderCompletedById(event.recordId)
            is RecordEvent.NavigateToEdit -> { /* Handled by UI */ }
            is RecordEvent.DeleteRecord -> {
                _state.value.timelineItems.firstOrNull { it.id == event.recordId }?.let { deleteRecord(it) }
            }
        }
    }

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            recordUseCases.deleteRecord(record)
        }
    }

    fun saveRecord(record: Record) {
        viewModelScope.launch {
            recordUseCases.saveRecord(record)
        }
    }

    fun onScreenHidden() {
        lastHiddenTimestamp = System.currentTimeMillis()
    }

    fun saveScrollState(index: Int, offset: Int) {
        savedScrollIndex = index
        savedScrollOffset = offset
    }

    fun shouldResetScroll(): Boolean {
        if (lastHiddenTimestamp == 0L) return true // First load
        val elapsed = System.currentTimeMillis() - lastHiddenTimestamp
        return elapsed > 40_000 // 40 seconds
    }

    private fun markReminderCompletedById(recordId: String) {
        val currentRecord = _state.value.timelineItems.firstOrNull { it.id == recordId } ?: return
        saveRecord(currentRecord.copy(isCompleted = true))
    }

    private fun loadScreenState(vehicleId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        getRecordScreenStateUseCase(vehicleId)
            .onEach { recordStateFromUseCase ->
                val uiState = computeUiStateFromTimeline(
                    vehicles = recordStateFromUseCase.vehicles,
                    selectedVehicleId = recordStateFromUseCase.selectedVehicleId ?: vehicleId,
                    timeline = recordStateFromUseCase.timelineItems,
                    isLoading = false,
                    error = null
                )
                _state.value = uiState
            }
            .catch { ex ->
                _state.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Unknown error") }
            }
            .launchIn(viewModelScope)
    }

    private fun computeUiStateFromTimeline(
        vehicles: List<Vehicle>,
        selectedVehicleId: String?,
        timeline: List<Record>,
        isLoading: Boolean,
        error: String?
    ): RecordState {
        val sortedTimeline = timeline.sortedByDescending { record ->
            if (record.isReminder) record.reminderDate ?: record.date else record.date
        }

        val now = Date()
        val latestUpcoming = timeline
            .filter { it.isReminder && it.reminderDate?.after(now) ?: false }
            .minByOrNull { it.reminderDate!! }

        val scrollIndex = sortedTimeline.indexOfFirst { record ->
            val recordDate = if (record.isReminder) record.reminderDate ?: record.date else record.date
            !recordDate.after(now)
        }.coerceAtLeast(0)

        return RecordState(
            vehicles = vehicles,
            selectedVehicleId = selectedVehicleId,
            timelineItems = sortedTimeline,
            latestUpcomingReminder = latestUpcoming,
            initialScrollIndex = scrollIndex,
            isLoading = isLoading,
            errorMessage = error
        )
    }
}
