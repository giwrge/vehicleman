package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// import com.vehicleman.ui.screens.AddEditRecordScreen // Commented out as it is a new component under development
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen
import com.vehicleman.ui.screens.Splashscreen

/**
 * The main Navigation Host for the application.
 */
@Composable
fun AppNavigation(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = "splash_route"
    ) {
        // 0. SPLASH SCREEN
        composable("splash_route") {
            Splashscreen(
                onTimeout = {
                    navHostController.popBackStack()
                    navHostController.navigate(NavDestinations.HOME_ROUTE)
                }
            )
        }


        // 1. HOME SCREEN
        composable(NavDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToVehicleForm = { vehicleId ->
                    navHostController.navigate(NavDestinations.addEditVehicleRoute(vehicleId))
                },
                onNavigateToEntryList = { _ ->
                    // Navigation to EntryList disabled for now
                }
            )
        }

        // 2. ADD/EDIT VEHICLE SCREEN
        composable(
            route = "${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}",
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType; nullable = false; defaultValue = "new"
                }
            )
        ) {
            AddEditVehicleScreen(
                onNavigateBack = { navHostController.popBackStack() }
            )
        }

        
        // 3. ENTRY LIST SCREEN (Θα μετονομαστεί σε RecordListScreen)
        composable(
            route = "${NavDestinations.ENTRY_LIST_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}",
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType; nullable = false
                }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString(NavDestinations.VEHICLE_ID_KEY) ?: ""

            EntryListScreen( // Θα γίνει RecordListScreen
                vehicleId = vehicleId,
                onNavigateBack = { navHostController.popBackStack() },
                onNavigateToEntryForm = { recordId -> // Χρησιμοποιεί το recordId
                    // Πλοήγηση στην νέα διαδρομή ADD_EDIT_RECORD_ROUTE
                    navHostController.navigate(NavDestinations.addEditRecordRoute(vehicleId, recordId))
                }
            )
        }

        // 4. ADD/EDIT RECORD SCREEN
        composable(
            route = "${NavDestinations.ADD_EDIT_RECORD_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}/{recordId}", // ΝΕΑ ΔΙΑΔΡΟΜΗ
            arguments = listOf(
                navArgument(NavDestinations.VEHICLE_ID_KEY) {
                    type = NavType.StringType; nullable = false
                },
                navArgument("recordId") { // Χρησιμοποιεί recordId
                    type = NavType.StringType; nullable = false; defaultValue = "new"
                }
            )
        ) {
            AddEditRecordScreen( // ΝΕΟ Component
                onNavigateBack = { navHostController.popBackStack() }
            )
        }
        
    }
}
