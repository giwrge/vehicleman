package com.vehicleman.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isNightMode = userPreferencesRepository.isNightMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setNightMode(isNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNightMode(isNightMode)
        }
    }
}