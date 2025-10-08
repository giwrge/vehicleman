package com.vehicleman.presentation.entries

sealed interface EntriesPanelEvent {
    data object AddNewVehicleClicked : EntriesPanelEvent
    // Η διαγραφή γίνεται πλέον με ID
    data class DeleteVehicleById(val vehicleId: String) : EntriesPanelEvent

    // Αφαιρούμε τις παλιές εκδηλώσεις:
    // data object ToggleSelectionMode : EntriesPanelEvent
    // data class ToggleVehicleSelection(val vehicleId: String) : EntriesPanelEvent
    // data object DeleteSelectedVehicles : EntriesPanelEvent
}