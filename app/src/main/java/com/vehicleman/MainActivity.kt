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

/**
 * Main Activity of the application.
 * Annotated with @AndroidEntryPoint to enable Hilt injection.
 */
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

/**
 * Defines all navigation routes for the application.
 */
object Routes {
    // Κύρια οθόνη (Vehicle List, Stats, Settings)
    const val HOME_SCREEN = "home"

    // Οθόνη καταχώρησης/τροποποίησης οχήματος. Χρειάζεται vehicleId.
    // 'new' για νέα καταχώρηση (από FAB), το ID για τροποποίηση (από Edit icon).
    const val ADD_EDIT_VEHICLE_SCREEN = "add_edit_vehicle_form/{vehicleId}"

    // Οθόνη καταχώρησης/τροποποίησης συμβάντος (π.χ. συντήρησης, βλάβης). Χρειάζεται vehicleId.
    const val ADD_EDIT_ENTRY_SCREEN = "add_edit_entry_form/{vehicleId}"

    // Helper function to build the vehicle form route
    fun addEditVehicleRoute(vehicleId: String) = "add_edit_vehicle_form/$vehicleId"

    // Helper function to build the entry form route
    fun addEditEntryRoute(vehicleId: String) = "add_edit_entry_form/$vehicleId"
}

/**
 * The main application screen that handles navigation using NavHost.
 */
@Composable
fun AppScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME_SCREEN
    ) {
        // 1. HOME SCREEN
        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                // FAB Tap -> New Vehicle
                onNavigateToAddVehicle = {
                    navController.navigate(Routes.addEditVehicleRoute("new"))
                },
                // Vehicle Card Tap -> Add/Edit Entry
                onNavigateToEntryForm = { vehicleId ->
                    navController.navigate(Routes.addEditEntryRoute(vehicleId))
                },
                // Edit Icon Tap -> Edit Vehicle
                onNavigateToVehicleForm = { vehicleId ->
                    navController.navigate(Routes.addEditVehicleRoute(vehicleId))
                }
            )
        }

        // 2. ADD/EDIT VEHICLE SCREEN
        composable(
            Routes.ADD_EDIT_VEHICLE_SCREEN,
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.StringType
                    nullable = true // Το 'new' είναι string, αλλά το αφήνουμε nullable για ευελιξία
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
            Routes.ADD_EDIT_ENTRY_SCREEN,
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.StringType
                    // To vehicleId δεν μπορεί να είναι null εδώ, καθώς προέρχεται από Tap σε υπάρχον όχημα
                }
            )
        ) {
            // Placeholder: Θα υλοποιηθεί στο επόμενο βήμα.
            // AddEditEntryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
