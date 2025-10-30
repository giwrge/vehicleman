package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TwinAppViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val user: StateFlow<User> = userPreferencesRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    fun becomeMainDriver() {
        viewModelScope.launch {
            val currentUser = user.first()
            if (currentUser.proLevel >= ProLevel.PRO_1 || currentUser.isTestMode) { // Only Pro users can be Main Drivers
                userPreferencesRepository.saveUser(
                    currentUser.copy(twinAppRole = TwinAppRole.MAIN_DRIVER)
                )
            }
        }
    }

    fun becomeSubDriver() {
        viewModelScope.launch {
            // In a real app, this would happen after a successful QR scan
            // We are simulating that the user becomes a SingleSubDriver by default
            userPreferencesRepository.saveUser(
                user.value.copy(
                    twinAppRole = TwinAppRole.SUB_DRIVER, 
                    subDriverType = SubDriverType.SINGLE // Default to single, Main Driver can change it
                )
            )
        }
    }
}