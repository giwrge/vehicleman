package com.vehicleman.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import com.vehicleman.domain.use_case.RecordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val recordUseCases: RecordUseCases
) : ViewModel() {

    val isNightMode = userPreferencesRepository.isNightMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val vehicleSortOrder = userPreferencesRepository.vehicleSortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VehicleSortOrder.ALPHABETICAL)

    val user = userPreferencesRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    fun setNightMode(isNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNightMode(isNightMode)
        }
    }

    fun setVehicleSortOrder(sortOrder: VehicleSortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.setVehicleSortOrder(sortOrder)
        }
    }

    fun exportData(format: String) {
        // TODO: Implement data export
    }

    fun populateDatabase() {
        viewModelScope.launch {
            recordUseCases.populateDatabaseWithFakeDataUseCase()
        }
    }
}
