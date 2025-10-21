package com.vehicleman.presentation.addeditvehicle

/**
 * UI State για την προβολή λίστας οχημάτων στο HomeScreen.
 */
data class AddEditVehiclePanelState(
    val vehicles: List<VehicleDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeVehicleId: String? = null, // To track the vehicle with the open "airflow" card
    val vehicleToDelete: VehicleDisplayItem? = null // To hold vehicle info for the confirmation dialog
)
