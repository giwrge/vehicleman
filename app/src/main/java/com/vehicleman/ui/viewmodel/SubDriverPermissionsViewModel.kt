package com.vehicleman.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.ui.navigation.NavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubDriverPermissionsState(
    val allVehicles: List<Vehicle> = emptyList(),
    val subDriver: User? = null, // We need to fetch the sub-driver's data
    val selectedType: SubDriverType = SubDriverType.SINGLE,
    val assignedVehicleIds: Set<String> = emptySet()
)

@HiltViewModel
class SubDriverPermissionsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository, // To update other users
    private val vehicleRepository: VehicleRepository, // To get all vehicles
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subDriverId: String = savedStateHandle[NavDestinations.SUB_DRIVER_ID_KEY]!!

    private val _uiState = MutableStateFlow(SubDriverPermissionsState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // NOTE: In a real app, you would fetch the specific sub-driver's data from a backend.
            // Here, we can't fetch other users' data, so we'll just work with the main driver's vehicles.
            vehicleRepository.getAllVehicles().collect { vehicles ->
                _uiState.value = _uiState.value.copy(allVehicles = vehicles)
            }
            // We would also load the sub-driver's current permissions here.
        }
    }

    fun onPermissionTypeChange(type: SubDriverType) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    fun onVehicleCheckedChange(vehicleId: String, isChecked: Boolean) {
        val currentAssigned = _uiState.value.assignedVehicleIds.toMutableSet()
        if (isChecked) {
            currentAssigned.add(vehicleId)
        } else {
            currentAssigned.remove(vehicleId)
        }
        _uiState.value = _uiState.value.copy(assignedVehicleIds = currentAssigned)
    }

    fun savePermissions() {
        viewModelScope.launch {
            // In a real app, you would save this data to the sub-driver's profile on the backend.
            // Here, we'll just print it to simulate the action.
            println("Saving permissions for $subDriverId: Type=${_uiState.value.selectedType}, Vehicles=${_uiState.value.assignedVehicleIds}")
        }
    }
}