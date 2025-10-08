package com.vehicleman.presentation.entries

/**
 * Sealed class representing all possible user actions/events on the Entries Panel.
 */
sealed class EntriesPanelEvent {
    /** Called when the user clicks the FAB to add a new vehicle. */
    object AddNewVehicleClicked : EntriesPanelEvent()

    /** Called when the user clicks to view/edit a specific vehicle. */
    data class VehicleClicked(val vehicleId: String) : EntriesPanelEvent()

    /** Toggles the selection mode on/off. */
    object ToggleSelectionMode : EntriesPanelEvent()

    /** Toggles the selection state of a specific vehicle. */
    data class ToggleVehicleSelection(val vehicleId: String) : EntriesPanelEvent()

    /** Deletes all currently selected vehicles. */
    object DeleteSelectedVehicles : EntriesPanelEvent()
}