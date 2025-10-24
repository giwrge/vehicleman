package com.vehicleman.ui.navigation

/**
 * Defines the navigation constants (routes and keys) used across the application.
 */
object NavDestinations {
    // Basic Routes
    const val SPLASH_SCREEN = "splash_screen"
    const val HOME_ROUTE = "home_route"
    const val ADD_EDIT_VEHICLE_ROUTE = "add_edit_vehicle_route"
    const val ENTRY_LIST_ROUTE = "entry_list_route"
    const val ADD_EDIT_ENTRY_ROUTE = "add_edit_entry_route"
    const val STATISTICS_ROUTE = "statistics_route"
    const val STATISTIC_VEHICLE_ROUTE = "statistic_vehicle_route"
    const val PREFERENCE_ROUTE = "preference_route"
    const val PRO_MODE_ROUTE = "pro_mode_route"
    const val SIGN_UP_ROUTE = "sign_up_route"

    // Argument Key
    const val VEHICLE_ID_KEY = "vehicleId"

    /**
     * Helper function to build the route for the Add/Edit Vehicle Form.
     */
    fun addEditVehicleRoute(vehicleId: String) = "$ADD_EDIT_VEHICLE_ROUTE/$vehicleId"

    /**
     * Helper function to build the route for the Vehicle Entry List.
     */
    fun entryListRoute(vehicleId: String) = "$ENTRY_LIST_ROUTE/$vehicleId"

    /**
     * Helper function to build the route for the Add/Edit Entry Form.
     */
    fun addEditEntryRoute(vehicleId: String, entryId: String? = null) =
        if (entryId != null) "$ADD_EDIT_ENTRY_ROUTE/$vehicleId/$entryId"
        else "$ADD_EDIT_ENTRY_ROUTE/$vehicleId/new"

    /**
     * Helper function to build the route for the Statistic Vehicle Screen.
     */
    fun statisticVehicleRoute(vehicleId: String) = "$STATISTIC_VEHICLE_ROUTE/$vehicleId"
}
