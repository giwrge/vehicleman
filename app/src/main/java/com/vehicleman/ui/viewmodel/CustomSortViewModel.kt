package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomSortViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // This is now the private mutable state for reordering
    private val _reorderedVehicles = MutableStateFlow<List<Vehicle>?>(null)

    // This is the public state exposed to the UI
    val vehicles: StateFlow<List<Vehicle>> = combine(
        vehicleRepository.getAllVehicles(),
        userPreferencesRepository.customVehicleOrder,
        _reorderedVehicles
    ) { allVehicles, customOrderString, reorderedList ->
        // If a reordered list exists, use it. Otherwise, use the sorted list from the repository.
        reorderedList ?: if (customOrderString.isNotBlank()) {
            val customOrderIds = customOrderString.split(",")
            allVehicles.sortedBy { vehicle ->
                customOrderIds.indexOf(vehicle.id).let { if (it == -1) Int.MAX_VALUE else it }
            }
        } else {
            allVehicles
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onMove(from: Int, to: Int) {
        val currentList = vehicles.value
        val mutableList = currentList.toMutableList()
        mutableList.add(to, mutableList.removeAt(from))
        _reorderedVehicles.value = mutableList
    }

    fun saveCustomOrder() {
        viewModelScope.launch {
            // Use the latest value from the vehicles StateFlow
            val customOrder = vehicles.value.joinToString(",") { it.id }
            userPreferencesRepository.setCustomVehicleOrder(customOrder)
        }
    }
}