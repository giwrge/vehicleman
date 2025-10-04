package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen

/**
 * Κεντρικό σύστημα πλοήγησης (NavHost) της εφαρμογής.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavDestinations.HOME_ROUTE // Ξεκινάμε από την κεντρική οθόνη
    ) {

        // ----------------------------------------------------
        // 1. HOME_ROUTE (Κεντρική Οθόνη με Bottom Navigation)
        // ----------------------------------------------------
        composable(NavDestinations.HOME_ROUTE) {
            // Η HomeScreen θα περιέχει το Bottom Navigation και θα εμφανίζει
            // τα tabs (Entries, Stats, Settings).
            HomeScreen(
                // 1. FAB click: Προσθήκη Νέου Οχήματος
                onNavigateToAddVehicle = {
                    // Ο ID είναι "new"
                    val route = NavDestinations.ADD_EDIT_VEHICLE_ROUTE + "/new"
                    navController.navigate(route)
                },
                // 2. Tap σε Όχημα: Πλοήγηση στη φόρμα καταχώρισης (Θα είναι EntryForm, προς το παρόν πάμε στο Vehicle Form)
                onNavigateToEntryForm = { vehicleId ->
                    // Χρησιμοποιούμε τον ID του οχήματος για επεξεργασία (προς το παρόν)
                    val route = NavDestinations.ADD_EDIT_VEHICLE_ROUTE + "/$vehicleId"
                    navController.navigate(route)
                },
                // 3. Edit Icon click: Πλοήγηση στη φόρμα επεξεργασίας οχήματος
                onNavigateToVehicleForm = { vehicleId ->
                    // Χρησιμοποιούμε τον ID του οχήματος για επεξεργασία
                    val route = NavDestinations.ADD_EDIT_VEHICLE_ROUTE + "/$vehicleId"
                    navController.navigate(route)
                }
            )
        }

        // ----------------------------------------------------\
        // 2. ADD_EDIT_VEHICLE_ROUTE (Φόρμα Προσθήκης/Επεξεργασίας)
        // ----------------------------------------------------\
        composable(
            route = NavDestinations.ADD_EDIT_VEHICLE_ROUTE + "/{${NavDestinations.VEHICLE_ID_KEY}}",
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType
                    defaultValue = "new" // Όταν προσθέτουμε νέο όχημα
                }
            )
        ) {
            AddEditVehicleScreen(
                onNavigateBack = { navController.popBackStack() } // Πίσω στην προηγούμενη οθόνη (HomeScreen)
            )
        }

        // --- ΠΡΟΣΟΧΗ: Θα προσθέσουμε αργότερα τις επιπλέον οθόνες των Tabs ---
    }
}
