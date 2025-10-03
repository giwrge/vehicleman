package com.vehicleman.ui.navigation

/**
 * Ορισμός των διαδρομών (routes) για την πλοήγηση.
 */
object NavDestinations {
    // Κεντρική Οθόνη: Περιέχει το Bottom Navigation Bar
    const val HOME_ROUTE = "home_route"

    // Φόρμα Προσθήκης/Επεξεργασίας Οχήματος
    // Χρησιμοποιεί το vehicleId ως argument (π.χ., "add_edit_vehicle/new" ή "add_edit_vehicle/1234")
    const val ADD_EDIT_VEHICLE_ROUTE = "add_edit_vehicle_route"
    const val VEHICLE_ID_KEY = "vehicleId"

    // Διαδρομές για τα Tabs (μέσα στο Bottom Navigation)
    sealed class HomeTabs(val route: String, val title: String) {
        object Statistics : HomeTabs("stats", "Στατιστικά")
        object Entries : HomeTabs("entries", "Καταχωρήσεις") // Λίστα Οχημάτων
        object Settings : HomeTabs("settings", "Ρυθμίσεις")
    }
}
