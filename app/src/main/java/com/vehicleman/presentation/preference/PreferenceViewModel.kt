package com.vehicleman.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.TranslateTitlePreference
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import com.vehicleman.domain.use_case.ClearAllDataExceptVehiclesUseCase
import com.vehicleman.domain.use_case.RecordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val recordUseCases: RecordUseCases,
    private val vehicleRepository: com.vehicleman.domain.repositories.VehicleRepository,
    private val clearAllDataExceptVehiclesUseCase: ClearAllDataExceptVehiclesUseCase
) : ViewModel() {

    val isNightMode = userPreferencesRepository.isNightMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val showAutoReminders = userPreferencesRepository.showAutoReminders // <-- ΝΕΑ ΠΡΟΣΘΗΚΗ
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val translateTitlePreference = userPreferencesRepository.translateTitlePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TranslateTitlePreference.ASK)

    val vehicleSortOrder = userPreferencesRepository.vehicleSortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VehicleSortOrder.ALPHABETICAL)

    val user = userPreferencesRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    val vehicles = vehicleRepository.getAllVehicles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<com.vehicleman.domain.model.Vehicle>())

    fun setNightMode(isNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNightMode(isNightMode)
        }
    }

    fun setShowAutoReminders(show: Boolean) { // <-- ΝΕΑ ΠΡΟΣΘΗΚΗ
        viewModelScope.launch {
            userPreferencesRepository.setShowAutoReminders(show)
        }
    }

    fun setTranslateTitlePreference(preference: TranslateTitlePreference) {
        viewModelScope.launch {
            userPreferencesRepository.setTranslateTitlePreference(preference)
        }
    }

    fun setVehicleSortOrder(sortOrder: VehicleSortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.setVehicleSortOrder(sortOrder)
        }
    }

    fun resetToFree() {
        viewModelScope.launch {
            val currentUser = user.first()
            userPreferencesRepository.saveUser(currentUser.copy(proLevel = ProLevel.NONE))
        }
    }

    fun exportData(format: String) {
        // TODO: Implement data export
    }

    fun populateDatabase(vehicleId: String? = null) {
        viewModelScope.launch {
            recordUseCases.populateDatabaseWithFakeDataUseCase(vehicleId)
        }
    }

    fun deleteFakeData(vehicleId: String) {
        viewModelScope.launch {
            recordUseCases.populateDatabaseWithFakeDataUseCase.deleteFakeData(vehicleId)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearAllDataExceptVehiclesUseCase()
        }
    }
}
