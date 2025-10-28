package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomSortViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    init {
        loadVehiclesWithCustomOrder()
    }

    private fun loadVehiclesWithCustomOrder() {
        viewModelScope.launch {
            val allVehicles = vehicleRepository.getAllVehicles().first()
            val customOrderString = userPreferencesRepository.customVehicleOrder.first()

            if (customOrderString.isNotBlank()) {
                val customOrderIds = customOrderString.split(",")
                val sortedVehicles = allVehicles.sortedBy { vehicle ->
                    customOrderIds.indexOf(vehicle.id).let { if (it == -1) Int.MAX_VALUE else it }
                }
                _vehicles.value = sortedVehicles
            } else {
                _vehicles.value = allVehicles
            }
        }
    }

    fun onMove(from: Int, to: Int) {
        val updatedList = _vehicles.value.toMutableList()
        updatedList.add(to, updatedList.removeAt(from))
        _vehicles.value = updatedList
    }

    fun saveCustomOrder() {
        viewModelScope.launch {
            val customOrder = _vehicles.value.joinToString(",") { it.id }
            userPreferencesRepository.setCustomVehicleOrder(customOrder)
        }
    }
}