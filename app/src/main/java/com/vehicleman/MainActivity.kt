package com.vehicleman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vehicleman.ui.navigation.AppNavigation // Import AppNavigation
import com.vehicleman.ui.theme.VehicleManTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleManTheme {
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

    // Χρησιμοποιούμε το AppNavigation Composable
    AppNavigation(navHostController = navController)
}

// ΣΗΜΕΙΩΣΗ: Διέγραψα τον κώδικα NavHost από εδώ και τον μετέφερα στο AppNavigation.kt
// (Όπως σου έδωσα στο προηγούμενο βήμα)