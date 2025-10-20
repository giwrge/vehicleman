package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen
import com.vehicleman.ui.screens.Splashscreen
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import com.vehicleman.ui.viewmodel.HomeViewModel

/**
 * The main Navigation Host for the application.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    addEditVehicleViewModel: AddEditVehicleViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.SPLASH_SCREEN
    ) {
        composable(NavDestinations.SPLASH_SCREEN) {
            Splashscreen(onNavigateToHome = {
                navController.navigate(NavDestinations.HOME_ROUTE) {
                    popUpTo(NavDestinations.SPLASH_SCREEN) { inclusive = true }
                }
            })
        }

        composable(NavDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToAddEditVehicle = {
                    navController.navigate(NavDestinations.ADD_EDIT_VEHICLE_ROUTE)
                },
                // ΚΑΝΕ ΤΑ "ΚΕΝΑ" ΓΙΑ ΝΑ ΜΗΝ ΔΟΥΛΕΥΟΥΝ
                onNavigateToRecord = { /* TODO: Re-enable later */ },
                onNavigateToStatistics = { /* TODO: Re-enable later */ },
                onNavigateToPreferences = { /* TODO: Re-enable later */ }
            )
        }

        composable(NavDestinations.ADD_EDIT_VEHICLE_ROUTE) {
            AddEditVehicleScreen(
                navController = navController
            )
        }

        /* // ΑΠΕΝΕΡΓΟΠΟΙΗΣΗ
        composable(NavDestinations.RECORD_ROUTE) {
            // ... RecordScreen() ...
        }
        */

        /* // ΑΠΕΝΕΡΓΟΠΟΙΗΣΗ
        composable(NavDestinations.STATISTICS_ROUTE) {
            // ... StatisticsScreen() ...
        }
        */
    }
}