package com.vehicleman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vehicleman.ui.screens.AddEditVehicleScreen
import com.vehicleman.ui.screens.HomeScreen
import com.vehicleman.ui.theme.VehicleManTheme
import dagger.hilt.android.AndroidEntryPoint
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.navigation.NavDestinations.HOME_ROUTE
import com.vehicleman.ui.navigation.NavDestinations.ADD_EDIT_VEHICLE_ROUTE
import com.vehicleman.ui.navigation.NavDestinations.VEHICLE_ID_KEY


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen()
                }
            }
        }
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()

    // ----------------------------------------------------------------------------------
    // *** Helper Functions (Πρόταση: Μεταφέρθηκαν στο NavDestinations.kt) ***
    // ----------------------------------------------------------------------------------
    // ΣΗΜΕΙΩΣΗ: Πρέπει να υπάρχουν στο NavDestinations.kt
    // const val ADD_EDIT_ENTRY_ROUTE = "add_edit_entry_route"
    // fun addEditVehicleRoute(vehicleId: String) = "$ADD_EDIT_VEHICLE_ROUTE/$vehicleId"
    // fun addEditEntryRoute(vehicleId: String) = "$ADD_EDIT_ENTRY_ROUTE/$vehicleId"
    // ----------------------------------------------------------------------------------

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE // Χρησιμοποιώ HOME_ROUTE από το NavDestinations
    ) {
        // 1. HOME SCREEN
        composable(HOME_ROUTE) { // Χρησιμοποιώ HOME_ROUTE
            HomeScreen(
                // FAB Tap -> New Vehicle
                onNavigateToAddVehicle = {
                    // Χρειάζεται η helper function από το NavDestinations
                    navController.navigate(NavDestinations.addEditVehicleRoute("new"))
                },
                // Vehicle Card Tap -> Add/Edit Entry
                onNavigateToEntryForm = { vehicleId ->
                    // Χρειάζεται η helper function από το NavDestinations
                    navController.navigate(NavDestinations.addEditEntryRoute(vehicleId))
                },
                // Edit Icon Tap -> Edit Vehicle
                onNavigateToVehicleForm = { vehicleId ->
                    // Χρειάζεται η helper function από το NavDestinations
                    navController.navigate(NavDestinations.addEditVehicleRoute(vehicleId))
                }
            )
        }

        // 2. ADD/EDIT VEHICLE SCREEN
        composable(
            route = "$ADD_EDIT_VEHICLE_ROUTE/{$VEHICLE_ID_KEY}", // Σωστό format για route με argument
            arguments = listOf(
                navArgument(VEHICLE_ID_KEY) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            AddEditVehicleScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. ADD/EDIT ENTRY SCREEN (Placeholder for next step)
        composable(
            route = "${NavDestinations.ADD_EDIT_ENTRY_ROUTE}/{$VEHICLE_ID_KEY}", // Υπόθεση: Πρέπει να ορίσεις ADD_EDIT_ENTRY_ROUTE στο NavDestinations
            arguments = listOf(
                navArgument(VEHICLE_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            // Placeholder
        }
    }
}