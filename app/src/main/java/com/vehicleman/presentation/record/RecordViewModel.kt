package com.vehicleman.presentation.record

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.use_case.RecordUseCases
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(RecordState())
    val state: State<RecordState> = _state

    val vehicleId: String = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY)!!

    init {
        getRecords(vehicleId)
    }

    private fun getRecords(vehicleId: String) {
        recordUseCases.getRecords(vehicleId)
            .onEach { records ->
                _state.value = state.value.copy(
                    records = records
                )
            }
            .launchIn(viewModelScope)
    }
}
