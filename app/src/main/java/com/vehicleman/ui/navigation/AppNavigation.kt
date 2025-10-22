// ui/navigation/AppNavigation.kt

package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen
import com.vehicleman.ui.screens.SplashScreen
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import com.vehicleman.ui.viewmodel.HomeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.SPLASH_SCREEN
    ) {
        composable(NavDestinations.SPLASH_SCREEN) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(NavDestinations.HOME_ROUTE) {
                    popUpTo(NavDestinations.SPLASH_SCREEN) { inclusive = true }
                }
            })
        }

        composable(NavDestinations.HOME_ROUTE) {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                onNavigateToAddEditVehicle = { vehicleId ->
                    val route = if (vehicleId != null) {
                        "${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/$vehicleId"
                    } else {
                        "${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/new"
                    }
                    navController.navigate(route)
                },
                onNavigateToRecord = { /* TODO: Implement */ }
            )
        }

        composable("${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
            AddEditVehicleScreen(
                navController = navController,
                vehicleId = vehicleId,
            )
        }
    }
}
