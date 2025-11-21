package com.vehicleman.presentation.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
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

    // Το vehicleId που έρχεται από το NavController (NavDestinations.VEHICLE_ID_KEY)
    private val vehicleIdArg: String? =
        savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)

    // Scroll / visibility helpers
    private var lastHiddenTimestamp: Long = 0L
    var savedScrollIndex: Int = 0
    var savedScrollOffset: Int = 0

    init {
        vehicleIdArg?.let { loadScreenState(it) }
    }

    // -----------------------------------------------------------------------
    //  Events από το UI
    // -----------------------------------------------------------------------

    fun onEvent(event: RecordEvent) {
        when (event) {
            is RecordEvent.VehicleSelected -> {
                loadScreenState(event.vehicleId)
            }

            is RecordEvent.Refresh -> {
                val currentVehicleId = _state.value.selectedVehicleId
                if (currentVehicleId != null) {
                    loadScreenState(currentVehicleId)
                } else {
                    vehicleIdArg?.let { loadScreenState(it) }
                }
            }

            is RecordEvent.MarkReminderCompleted -> {
                markReminderCompleted(event.recordId)
            }

            is RecordEvent.DeleteRecord -> {
                deleteById(event.recordId)
            }

            is RecordEvent.ToggleExpandRecord,
            is RecordEvent.NavigateToEdit -> {
                // Αυτά τα χειρίζεται το UI (navigation / expand), όχι το ViewModel
            }
        }
    }

    // -----------------------------------------------------------------------
    //  Διαγραφή / Αποθήκευση
    // -----------------------------------------------------------------------

    private fun deleteById(recordId: String) {
        val record = _state.value.timelineItems.firstOrNull { it.id == recordId }
        if (record != null) {
            deleteRecord(record)
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

    // -----------------------------------------------------------------------
    //  Υπενθυμίσεις
    // -----------------------------------------------------------------------

    private fun markReminderCompleted(recordId: String) {
        val rec = _state.value.timelineItems.firstOrNull { it.id == recordId } ?: return
        saveRecord(rec.copy(isCompleted = true))
    }

    // -----------------------------------------------------------------------
    //  Visibility / Scroll State
    // -----------------------------------------------------------------------

    fun onScreenHidden() {
        lastHiddenTimestamp = System.currentTimeMillis()
    }

    fun saveScrollState(index: Int, offset: Int) {
        savedScrollIndex = index
        savedScrollOffset = offset
    }

    fun shouldResetScroll(): Boolean {
        if (lastHiddenTimestamp == 0L) return true  // πρώτη φορά
        val elapsed = System.currentTimeMillis() - lastHiddenTimestamp
        return elapsed > 40_000   // πάνω από 40sec → reset
    }

    // -----------------------------------------------------------------------
    //  Φόρτωση State (με auto-reminders μέσω UseCase)
    // -----------------------------------------------------------------------

    private fun loadScreenState(vehicleId: String) {

        _state.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        getRecordScreenStateUseCase(vehicleId)
            .onEach { result ->

                val timeline = result.timelineItems
                val now = Date()

                // Πρώτη μελλοντική υπενθύμιση για sticky note
                val latestUpcoming = timeline
                    .filter { it.isReminder && it.reminderDate?.after(now) == true }
                    .minByOrNull { it.reminderDate!!.time }

                // Index για scroll στο "σήμερα"
                val scrollIndex = timeline.indexOfFirst { record ->
                    val d = record.reminderDate ?: record.date
                    !d.after(now)
                }.coerceAtLeast(0)

                _state.value = RecordState(
                    vehicles = result.vehicles,
                    selectedVehicleId = result.selectedVehicleId,
                    timelineItems = timeline,
                    latestUpcomingReminder = latestUpcoming,
                    initialScrollIndex = scrollIndex,
                    isLoading = false,
                    errorMessage = null
                )
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
}
