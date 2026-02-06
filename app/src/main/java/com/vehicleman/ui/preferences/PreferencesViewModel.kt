package com.vehicleman.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.TranslateTitlePreference
import com.vehicleman.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val translateTitlePreference = userPreferencesRepository.translateTitlePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TranslateTitlePreference.ASK)

    val showAutoReminders = userPreferencesRepository.showAutoReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setTranslateTitlePreference(preference: TranslateTitlePreference) {
        viewModelScope.launch {
            userPreferencesRepository.setTranslateTitlePreference(preference)
        }
    }

    fun setShowAutoReminders(show: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowAutoReminders(show)
        }
    }
}
