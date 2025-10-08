package com.vehicleman.ui.panels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close // ΣΩΣΤΟ IMPORT
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vehicleman.presentation.entries.EntriesPanelEvent
import com.vehicleman.presentation.entries.EntriesPanelState
import com.vehicleman.presentation.entries.VehicleDisplayItem
import androidx.compose.ui.graphics.Color
// Δεν χρειάζεται πλέον το import androidx.compose.material.icons.filled.ArrowForward

/**
 * Displays the list of vehicles and handles selection/deletion features.
 */
@Composable
fun EntriesPanel(
    state: EntriesPanelState,
    onEvent: (EntriesPanelEvent) -> Unit,
    onVehicleClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            if (state.isSelectionMode) {
                SelectionTopAppBar(state, onEvent)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                state.vehicles.isEmpty() && !state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Δεν υπάρχουν καταχωρημένα οχήματα.", style = MaterialTheme.typography.titleMedium)
                    }
                }
                else -> {
                    VehicleList(state, onEvent, onVehicleClicked)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionTopAppBar(
    state: EntriesPanelState,
    onEvent: (EntriesPanelEvent) -> Unit
) {
    val selectedCount = state.selectedVehicleIds.size
    TopAppBar(
        title = {
            Text(text = "$selectedCount Επιλεγμένα")
        },
        navigationIcon = {
            IconButton(onClick = { onEvent(EntriesPanelEvent.ToggleSelectionMode) }) {
                Icon(Icons.Default.Close, contentDescription = "Ακύρωση Επιλογής")
            }
        },
        actions = {
            if (selectedCount > 0) {
                IconButton(onClick = { onEvent(EntriesPanelEvent.DeleteSelectedVehicles) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Διαγραφή Επιλεγμένων")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun VehicleList(
    state: EntriesPanelState,
    onEvent: (EntriesPanelEvent) -> Unit,
    onVehicleClicked: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(state.vehicles, key = { it.id }) { item ->
            VehicleEntryItem(
                vehicle = item,
                isSelectionMode = state.isSelectionMode,
                onItemClick = {
                    if (state.isSelectionMode) {
                        onEvent(EntriesPanelEvent.ToggleVehicleSelection(item.id))
                    } else {
                        onVehicleClicked(item.id)
                    }
                },
                onLongClick = {
                    if (!state.isSelectionMode) {
                        onEvent(EntriesPanelEvent.ToggleSelectionMode)
                        onEvent(EntriesPanelEvent.ToggleVehicleSelection(item.id))
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun VehicleEntryItem(
    vehicle: VehicleDisplayItem,
    isSelectionMode: Boolean,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // ΔΙΟΡΘΩΣΗ: Χρησιμοποιούμε το default χρώμα του MaterialTheme (surface) ως fallback.
    // Αυτό είναι πιο ασφαλές από το να προσπαθούμε να αποκτήσουμε πρόσβαση στο CardDefaults.
    val defaultColor = MaterialTheme.colorScheme.surface

    val containerColor: Color = if (vehicle.isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        defaultColor // <--- Χρησιμοποιούμε το ασφαλές default χρώμα
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection mode
            if (isSelectionMode) {
                Checkbox(
                    checked = vehicle.isSelected,
                    onCheckedChange = { onItemClick() }, // Toggle on check change
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // Vehicle Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = vehicle.makeModel,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Πινακίδα: ${vehicle.licensePlate}",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Arrow icon (only outside selection mode)
            if (!isSelectionMode) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Επεξεργασία",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}