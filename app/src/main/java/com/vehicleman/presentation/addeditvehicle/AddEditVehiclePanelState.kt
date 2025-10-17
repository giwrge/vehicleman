package com.vehicleman.presentation.addeditvehicle

/**
 * UI State για την προβολή λίστας οχημάτων στο HomeScreen.
 */
data class AddEditVehiclePanelState(
    val vehicles: List<VehicleDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
