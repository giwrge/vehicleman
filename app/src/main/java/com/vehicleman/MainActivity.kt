package com.vehicleman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vehicleman.ui.navigation.AppNavigation
import com.vehicleman.ui.theme.VehicleManTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the VehicleMan app.
 * Serves as the entry point and hosts the Jetpack Compose navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VehicleManTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavigation now handles its own NavController and ViewModels internally
                    AppNavigation()
                }
            }
        }
    }
}