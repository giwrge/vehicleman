package com.vehicleman.ui.navigation

/**
 * Defines the navigation constants (routes and keys) used across the application.
 */
object NavDestinations {
    // Basic Routes
    const val HOME_ROUTE = "home_route"
    const val ADD_EDIT_VEHICLE_ROUTE = "add_edit_vehicle_route"
    const val ADD_EDIT_ENTRY_ROUTE = "add_edit_entry_route" // Θα το χρησιμοποιήσουμε αργότερα

    // Argument Key
    const val VEHICLE_ID_KEY = "vehicleId"

    /**
     * Helper function to build the route for the Add/Edit Vehicle Form.
     * @param vehicleId The ID of the vehicle to edit, or "new" for adding a new one.
     */
    fun addEditVehicleRoute(vehicleId: String) = "$ADD_EDIT_VEHICLE_ROUTE/$vehicleId"

    /**
     * Helper function to build the route for the Add/Edit Entry Form (Future feature).
     */
    fun addEditEntryRoute(vehicleId: String) = "$ADD_EDIT_ENTRY_ROUTE/$vehicleId"
}