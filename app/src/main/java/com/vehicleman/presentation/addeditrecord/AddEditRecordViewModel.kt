package com.vehicleman.presentation.addeditrecord

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.use_case.RecordUseCases
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(AddEditRecordState())
    val state: State<AddEditRecordState> = _state

    private val vehicleId: String = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)!!
    private var recordId: String? = null

    init {
        savedStateHandle.get<String>("recordId")?.let {
            if (it != "new") {
                viewModelScope.launch {
                    recordUseCases.getRecord(it)?.also {
                        recordId = it.id
                        _state.value = state.value.copy(
                            date = it.date,
                            odometer = it.odometer.toString(),
                            isExpense = it.isExpense,
                            title = it.title,
                            description = it.description ?: "",
                            amount = it.amount?.toString() ?: "",
                            reminderDate = it.reminderDate
                        )
                    }
                }
            } else {
                viewModelScope.launch {
                    _state.value = state.value.copy(
                        odometer = recordUseCases.getLatestOdometer(vehicleId)?.toString() ?: ""
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditRecordEvent) {
        when (event) {
            is AddEditRecordEvent.OnDateChanged -> _state.value = state.value.copy(date = event.date)
            is AddEditRecordEvent.OnOdometerChanged -> _state.value = state.value.copy(odometer = event.odometer)
            is AddEditRecordEvent.OnIsExpenseChanged -> _state.value = state.value.copy(isExpense = event.isExpense)
            is AddEditRecordEvent.OnTitleChanged -> _state.value = state.value.copy(title = event.title)
            is AddEditRecordEvent.OnDescriptionChanged -> _state.value = state.value.copy(description = event.description)
            is AddEditRecordEvent.OnAmountChanged -> _state.value = state.value.copy(amount = event.amount)
            is AddEditRecordEvent.OnReminderDateChanged -> _state.value = state.value.copy(reminderDate = event.reminderDate)
            is AddEditRecordEvent.OnSaveRecord -> {
                viewModelScope.launch {
                    recordUseCases.saveRecord(
                        Record(
                            id = recordId ?: "",
                            vehicleId = vehicleId,
                            date = state.value.date,
                            odometer = state.value.odometer.toIntOrNull() ?: 0,
                            isExpense = state.value.isExpense,
                            title = state.value.title,
                            description = state.value.description,
                            amount = state.value.amount.toDoubleOrNull(),
                            reminderDate = state.value.reminderDate
                        )
                    )
                }
            }
        }
    }
}
