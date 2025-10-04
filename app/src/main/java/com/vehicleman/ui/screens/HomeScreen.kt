package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vehicleman.ui.panels.EntriesPanel

@Composable
fun HomeScreen(
    onNavigateToAddVehicle: () -> Unit = {},
    onNavigateToEntryForm: (String) -> Unit = {},
    onNavigateToVehicleForm: (String) -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddVehicle) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            EntriesPanel(
                onVehicleTap = { id -> onNavigateToEntryForm(id) },
                onVehicleEdit = { id -> onNavigateToVehicleForm(id) }
            )
        }
    }
}
