package com.vehicleman.ui.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import com.vehicleman.ui.navigation.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    fromScreen: String?,
    fromId: String?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        var totalDragAmount by remember { mutableStateOf(0f) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { totalDragAmount = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDragAmount += dragAmount
                        },
                        onDragEnd = {
                            if (totalDragAmount > 30) { // Swipe Right
                                val route = when (fromScreen) {
                                    NavDestinations.HOME_IDENTIFIER -> NavDestinations.HOME_ROUTE
                                    NavDestinations.RECORD_IDENTIFIER -> NavDestinations.entryListRoute(fromId ?: "")
                                    else -> NavDestinations.HOME_ROUTE // Default fallback
                                }
                                navController.navigate(route)
                            }
                        }
                    )
                }
        ) {
            Text("Statistics screen content goes here.")
        }
    }
}