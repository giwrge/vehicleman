package com.vehicleman.ui.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.presentation.record.RecordViewModel
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.screens.components.RecordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    navController: NavController, 
    onNavigateToAddEditRecord: (String, String?) -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
    vehicleId: String?
) {
    val state by viewModel.state

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.vehicleId?.let { vId ->
                        onNavigateToAddEditRecord(vId, "new")
                    }
                }
            ) {
                Icon(Icons.Filled.Add, "Add record")
            }
        }
    ) { padding ->
        var totalDragAmount by remember { mutableStateOf(0f) }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDragAmount = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragAmount += dragAmount
                    },
                    onDragEnd = {
                        if (totalDragAmount < -30) { // Swipe Left
                            navController.navigate(NavDestinations.statisticsRoute(NavDestinations.RECORD_IDENTIFIER, vehicleId))
                        } else if (totalDragAmount > 30) { // Swipe Right
                            navController.navigate(NavDestinations.preferenceRoute(NavDestinations.RECORD_IDENTIFIER, vehicleId))
                        }
                    }
                )
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.records) {
                    record ->
                    RecordItem(
                        record = record,
                        onClick = { 
                            viewModel.vehicleId?.let { vId ->
                                onNavigateToAddEditRecord(vId, record.id)
                            } 
                        }
                    )
                }
            }
        }
    }
}