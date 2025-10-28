package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SingupFormState(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val country: String = "",
    val email: String = "",
    val verificationCode: String = "",
    val isCodeSent: Boolean = false,
    val isLoading: Boolean = false,
    val isSignupSuccessful: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SingupViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SingupFormState())
    val uiState = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onFirstNameChanged(firstName: String) {
        _uiState.update { it.copy(firstName = firstName) }
    }

    fun onLastNameChanged(lastName: String) {
        _uiState.update { it.copy(lastName = lastName) }
    }

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun onCityChanged(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    fun onCountryChanged(country: String) {
        _uiState.update { it.copy(country = country) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onVerificationCodeChanged(code: String) {
        _uiState.update { it.copy(verificationCode = code) }
    }

    fun sendVerificationCode() {
        // Simulate sending a code
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            // In a real app, you would call an API here
            kotlinx.coroutines.delay(1000) // Simulate network delay
            _uiState.update { it.copy(isCodeSent = true, isLoading = false) }
        }
    }

    fun verifyCodeAndSignup() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Simulate verification
            
            // This is a dummy verification. In a real app, check against the sent code.
            if (_uiState.value.verificationCode == "123456") { 
                val user = User(
                    status = UserStatus.SIGNED_UP,
                    username = _uiState.value.username,
                    firstName = _uiState.value.firstName,
                    lastName = _uiState.value.lastName,
                    address = _uiState.value.address,
                    city = _uiState.value.city,
                    country = _uiState.value.country,
                    email = _uiState.value.email
                )
                userPreferencesRepository.saveUser(user)
                _uiState.update { it.copy(isSignupSuccessful = true, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid verification code") }
            }
        }
    }
}