package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelState
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για τη HomeScreen — διαχειρίζεται τη λίστα οχημάτων.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVehiclePanelState())
    val state: StateFlow<AddEditVehiclePanelState> = _state

    private var activeVehicleId: String? = null

    init {
        loadVehicles()
    }

    fun onEvent(event: AddEditVehiclePanelEvent) {
        when (event) {
            is AddEditVehiclePanelEvent.DeleteVehicleById -> deleteVehicleById(event.vehicleId)
            is AddEditVehiclePanelEvent.RefreshVehicleList -> loadVehicles()
            is AddEditVehiclePanelEvent.ToggleAirflowCard -> toggleAirflowCard(event.vehicleId)
            else -> Unit
        }
    }

    /** Φόρτωση όλων των οχημάτων **/
    private fun loadVehicles() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                vehicleRepository.getAllVehicles().collectLatest { vehicles ->
                    val uiList = vehicles.map { it.toDisplayItem() }
                    _state.update {
                        it.copy(
                            vehicles = uiList,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Σφάλμα φόρτωσης οχημάτων")
                }
            }
        }
    }

    /** Διαγραφή οχήματος βάσει ID **/
    private fun deleteVehicleById(vehicleId: String) {
        viewModelScope.launch {
            try {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                vehicle?.let { vehicleRepository.deleteVehicle(it) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Αποτυχία διαγραφής οχήματος")
                }
            }
        }
    }

    /** Ενεργοποίηση ή απόκρυψη Airflow Card **/
    private fun toggleAirflowCard(vehicleId: String) {
        activeVehicleId = if (activeVehicleId == vehicleId) null else vehicleId
        _state.update { current ->
            current.copy(
                vehicles = current.vehicles.map {
                    it.copy(isActive = it.id == activeVehicleId)
                }
            )
        }
    }

    /** Μετατροπή Vehicle → VehicleDisplayItem **/
    private fun Vehicle.toDisplayItem(): VehicleDisplayItem {
        return VehicleDisplayItem(
            id = this.id,
            name = "${this.make} ${this.model}",
            makeModel = "${this.make} • ${this.model}",
            licensePlate = this.plateNumber,
            odometerText = "${this.odometer} km",
            isActive = false
        )
    }
}
