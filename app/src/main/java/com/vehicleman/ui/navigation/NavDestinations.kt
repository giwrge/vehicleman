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

    const val ADD_EDIT_RECORD_ROUTE = "add_edit_record_route"
    const val STATISTICS_ROUTE = "statistics_route"
    const val DETAILED_ANALYSIS_ROUTE = "detailed_analysis_route" // ✅ New Unified Analysis Route
    const val PREFERENCE_ROUTE = "preference_route"
    const val PRO_MODE_ROUTE = "pro_mode_route"
    const val SIGN_UP_ROUTE = "sign_up_route"
    const val USERS_ROUTE = "users_route"
    const val CUSTOM_SORT_ROUTE = "custom_sort_route"
    const val DRIVERS_ROUTE = "drivers_route"
    const val TWIN_APP_SETUP_ROUTE = "twin_app_setup_route"
    const val SUB_DRIVER_PERMISSIONS_ROUTE = "sub_driver_permissions_route"
    const val BACKUP_ROUTE = "backup_route"
    const val RESTORE_ROUTE = "restore_route"
    const val IMPORT_WIZARD_ROUTE = "import_wizard_route"

    // Argument Keys
    const val VEHICLE_ID_KEY = "vehicleId"
    const val ANALYSIS_TYPE_KEY = "analysisType" // "vehicle" or "user"
    const val ANALYSIS_ID_KEY = "analysisId"
    const val RECORD_ID_KEY = "recordId"
    const val FROM_REMINDER_KEY = "fromReminder"
    const val SUB_DRIVER_ID_KEY = "subDriverId"
    const val FROM_SCREEN_KEY = "fromScreen"
    const val FROM_ID_KEY = "fromId"

    // Screen Identifiers
    const val HOME_IDENTIFIER = "home"
    const val RECORD_IDENTIFIER = "record"
    
    // Analysis Types
    const val TYPE_VEHICLE = "vehicle"
    const val TYPE_USER = "user"

    fun addEditVehicleRoute(vehicleId: String) = "$ADD_EDIT_VEHICLE_ROUTE/$vehicleId"
    fun entryListRoute(vehicleId: String) = "$ENTRY_LIST_ROUTE/$vehicleId"
    fun detailedAnalysisRoute(type: String, id: String) = "$DETAILED_ANALYSIS_ROUTE/$type/$id"

    fun statisticsRoute(fromScreen: String, fromId: String? = null) = 
        "$STATISTICS_ROUTE/$fromScreen/${fromId ?: "-1"}"

    fun preferenceRoute(fromScreen: String, fromId: String? = null) = 
        "$PREFERENCE_ROUTE/$fromScreen/${fromId ?: "-1"}"

    fun addEditEntryRoute(vehicleId: String, recordId: String? = null, fromReminder: Boolean = false): String {
        val rId = recordId ?: "new"
        return "$ADD_EDIT_ENTRY_ROUTE/$vehicleId/$rId?$FROM_REMINDER_KEY=$fromReminder"
    }

    fun addEditRecordRoute(vehicleId: String, recordId: String? = null): String =
        if (recordId == null) "$ADD_EDIT_RECORD_ROUTE/$vehicleId/new" else "$ADD_EDIT_RECORD_ROUTE/$vehicleId/$recordId"
}
