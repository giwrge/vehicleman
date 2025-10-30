package com.vehicleman.presentation.addeditvehicle

import com.vehicleman.domain.model.Vehicle

/**
 * UI State for the vehicle list panel.
 */
data class AddEditVehiclePanelState(
    val vehicles: List<VehicleDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeVehicleId: String? = null, // To track the vehicle with the open "airflow" card
    val vehicleToDelete: VehicleDisplayItem? = null // To hold vehicle info for the confirmation dialog
)
