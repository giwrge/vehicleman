package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen

/**
 * The main Navigation Host for the application.
 *
 * @param navHostController The controller for navigation actions.
 */
@Composable
fun AppNavigation(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = NavDestinations.HOME_ROUTE
    ) {
        // 1. HOME SCREEN
        composable(NavDestinations.HOME_ROUTE) {
            HomeScreen(
                // Πλοήγηση στη φόρμα για Προσθήκη ("new") ή Επεξεργασία (με ID)
                onNavigateToVehicleForm = { vehicleId ->
                    navHostController.navigate(NavDestinations.addEditVehicleRoute(vehicleId))
                }
                // *** ΣΗΜΕΙΩΣΗ ***: Δεν υπάρχει πλέον onNavigateToEntryForm.
            )
        }

        // 2. ADD/EDIT VEHICLE SCREEN
        composable(
            route = "${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}",
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType
                    nullable = false
                    defaultValue = "new" // Default for Add mode
                }
            )
        ) {
            AddEditVehicleScreen(
                onNavigateBack = {
                    navHostController.popBackStack()
                }
            )
        }

        // 3. ADD/EDIT ENTRY SCREEN (Placeholder for next step - Χρειάζεται για να περάσει το build)
        composable(
            route = "${NavDestinations.ADD_EDIT_ENTRY_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}",
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            // Αυτό είναι κενό placeholder μέχρι να το υλοποιήσουμε
        }
    }
}