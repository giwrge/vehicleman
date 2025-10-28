package com.vehicleman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vehicleman.ui.preferences.PreferenceScreen
import com.vehicleman.ui.screens.AddEditRecordScreen
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.BackupScreen
import com.vehicleman.ui.screens.CustomSortScreen
import com.vehicleman.ui.screens.DriversScreen
import com.vehicleman.ui.screens.HomeScreen
import com.vehicleman.ui.screens.ImportWizardScreen
import com.vehicleman.ui.screens.PromodeScreen
import com.vehicleman.ui.screens.RecordScreen
import com.vehicleman.ui.screens.RestoreScreen
import com.vehicleman.ui.screens.SingupScreen
import com.vehicleman.ui.screens.SplashScreen
import com.vehicleman.ui.screens.StatisticVehicleScreen
import com.vehicleman.ui.screens.StatisticsScreen
import com.vehicleman.ui.screens.SubDriverPermissionsScreen
import com.vehicleman.ui.screens.TwinAppSetupScreen
import com.vehicleman.ui.screens.UsersScreen
import com.vehicleman.ui.viewmodel.HomeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    isNightMode: Boolean
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
                onNavigateToRecord = { vehicleId ->
                    navController.navigate(NavDestinations.entryListRoute(vehicleId))
                },
                onNavigateToStatistics = {
                    navController.navigate(NavDestinations.STATISTICS_ROUTE)
                },
                onNavigateToPreferences = {
                    navController.navigate(NavDestinations.PREFERENCE_ROUTE)
                },
                onNavigateToProMode = {
                    navController.navigate(NavDestinations.PRO_MODE_ROUTE)
                },
                onNavigateToSignUp = {
                    navController.navigate(NavDestinations.SIGN_UP_ROUTE)
                },
                isNightMode = isNightMode
            )
        }

        composable("${NavDestinations.ADD_EDIT_VEHICLE_ROUTE}/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
            AddEditVehicleScreen(
                navController = navController,
                vehicleId = vehicleId,
                isNightMode = isNightMode
            )
        }

        composable(
            route = "${NavDestinations.ENTRY_LIST_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}",
        ) {
            RecordScreen(
                navController = navController,
                onNavigateToAddEditRecord = { vehicleId, recordId ->
                    navController.navigate(NavDestinations.addEditEntryRoute(vehicleId, recordId))
                }
            )
        }

        composable( route = "${NavDestinations.ADD_EDIT_ENTRY_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}/{recordId}",
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")

            AddEditRecordScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
                onRecordSaved = {
                    if (recordId == "new") {
                        val vehicleId =
                            backStackEntry.arguments?.getString(NavDestinations.VEHICLE_ID_KEY)!!
                        navController.navigate(NavDestinations.entryListRoute(vehicleId)) {
                            popUpTo(backStackEntry.destination.id) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(NavDestinations.STATISTICS_ROUTE) {
            StatisticsScreen(navController = navController)
        }

        composable(
            route = "${NavDestinations.STATISTIC_VEHICLE_ROUTE}/{${NavDestinations.VEHICLE_ID_KEY}}"
        ) {
            StatisticVehicleScreen(navController = navController)
        }

        composable(NavDestinations.PREFERENCE_ROUTE) {
            PreferenceScreen(navController = navController)
        }

        composable(NavDestinations.PRO_MODE_ROUTE) {
            PromodeScreen(navController = navController)
        }

        composable(NavDestinations.SIGN_UP_ROUTE) {
            SingupScreen(navController = navController)
        }

        composable(NavDestinations.USERS_ROUTE) {
            UsersScreen(navController = navController)
        }

        composable(NavDestinations.CUSTOM_SORT_ROUTE) {
            CustomSortScreen(navController = navController)
        }
        
        composable(NavDestinations.DRIVERS_ROUTE) {
            DriversScreen(navController = navController)
        }

        composable(NavDestinations.TWIN_APP_SETUP_ROUTE) {
            TwinAppSetupScreen(navController = navController)
        }
        
        composable("${NavDestinations.SUB_DRIVER_PERMISSIONS_ROUTE}/{${NavDestinations.SUB_DRIVER_ID_KEY}}") { backStackEntry ->
            val subDriverId = backStackEntry.arguments?.getString(NavDestinations.SUB_DRIVER_ID_KEY)!!
            SubDriverPermissionsScreen(navController = navController, subDriverId = subDriverId)
        }
        
        composable(NavDestinations.BACKUP_ROUTE) {
            BackupScreen(navController = navController)
        }
        
        composable(NavDestinations.RESTORE_ROUTE) {
            RestoreScreen(navController = navController)
        }

        composable(NavDestinations.IMPORT_WIZARD_ROUTE) {
            ImportWizardScreen(navController = navController)
        }
    }
}
