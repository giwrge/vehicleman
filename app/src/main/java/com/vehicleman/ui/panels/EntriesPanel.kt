package com.vehicleman.ui.panels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.ui.viewmodels.EntriesEvent
import com.vehicleman.ui.viewmodels.EntriesViewModel

// Custom Color (Σκούρο Γκρι/Μπλε) for Light Mode
private val DarkBlueGray = Color(0xFF424242)

/**
 * Panel 2: List of Registered Vehicles and Timeline.
 *
 * @param onVehicleTap The function called when a Vehicle is Tapped
 * (navigation to the ADD/EDIT ENTRY screen).
 * @param onVehicleEdit The function called when the user taps the EDIT icon
 * (navigation to the ADD/EDIT VEHICLE screen).
 * @param viewModel The ViewModel providing the data.
 */
@Composable
fun EntriesPanel(
    onVehicleTap: (vehicleId: String) -> Unit,
    onVehicleEdit: (vehicleId: String) -> Unit, // ΝΕΑ ΛΕΙΤΟΥΡΓΙΑ: Τροποποίηση Οχήματος
    viewModel: EntriesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 1. App Banner & Actions (Top Right)
        AppBannerAndActions(
            isSelectionMode = state.isSelectionMode,
            selectedVehicleIds = state.selectedVehicleIds, // Προσθήκη για έλεγχο
            onExitSelection = { viewModel.onEvent(EntriesEvent.ExitSelectionMode) },
            onDeleteSelected = { viewModel.onEvent(EntriesEvent.DeleteSelectedVehicles) },
            // Λειτουργία τροποποίησης
            onEditSelected = {
                // Βεβαιωθείτε ότι υπάρχει μόνο ένα επιλεγμένο όχημα πριν την κλήση
                if (state.selectedVehicleIds.size == 1) {
                    onVehicleEdit(state.selectedVehicleIds.first())
                    viewModel.onEvent(EntriesEvent.ExitSelectionMode) // Έξοδος από Selection Mode μετά την πλοήγηση
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Vehicle List
        if (state.vehicles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Δεν υπάρχουν καταχωρημένα οχήματα.",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            VehicleList(
                vehicles = state.vehicles,
                isSelectionMode = state.isSelectionMode,
                selectedVehicleIds = state.selectedVehicleIds,
                onCardClick = onVehicleTap,
                onLongClick = { viewModel.onEvent(EntriesEvent.ToggleVehicleSelection(it)) },
                onToggleSelection = { viewModel.onEvent(EntriesEvent.ToggleVehicleSelection(it)) }
            )
        }
    }
}

/**
 * Displays the App Banner and action buttons (Delete, Modify)
 * when Selection Mode is active.
 */
@Composable
fun AppBannerAndActions(
    isSelectionMode: Boolean,
    selectedVehicleIds: Set<String>, // Νέα παράμετρος
    onExitSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onEditSelected: () -> Unit // Νέα παράμετρος
) {
    // Ενεργοποίηση κουμπιού τροποποίησης μόνο αν έχει επιλεγεί ακριβώς 1 όχημα
    val isEditEnabled = selectedVehicleIds.size == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo / Title (Independent of Selection Mode)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_icon_placeholder), // Your Icon
                contentDescription = "Λογότυπο Εφαρμογής",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary // Icon Color
            )
            Text(
                text = stringResource(id = R.string.app_name), // Assuming stringResource(R.string.app_name)
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp),
                color = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        // Flow Buttons (Top Right - Visible only in Selection Mode)
        AnimatedVisibility(visible = isSelectionMode) {
            Row {
                // Modify Button (Wrench) - Ενεργό μόνο για μία επιλογή
                IconButton(
                    onClick = onEditSelected,
                    enabled = isEditEnabled // Έλεγχος ενεργοποίησης
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Τροποποίηση",
                        tint = if (isEditEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Delete Button (Trash Can)
                IconButton(onClick = onDeleteSelected) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Διαγραφή",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Exit Button (Check)
                IconButton(onClick = onExitSelection) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_placeholder), // Hypothetical Check Icon
                        contentDescription = "Ολοκλήρωση Επιλογής",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Displays the list of vehicles.
 */
@Composable
fun VehicleList(
    vehicles: List<Vehicle>,
    isSelectionMode: Boolean,
    selectedVehicleIds: Set<String>,
    onCardClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(vehicles, key = { _, vehicle -> vehicle.id }) { index, vehicle ->
            val isSelected = selectedVehicleIds.contains(vehicle.id)

            VehicleCard(
                vehicle = vehicle,
                index = index + 1, // Sequential registration number
                isSelectionMode = isSelectionMode,
                isSelected = isSelected,
                onTap = {
                    if (isSelectionMode) {
                        onToggleSelection(vehicle.id)
                    } else {
                        onCardClick(vehicle.id) // Navigate to the entry/event screen
                    }
                },
                onLongTap = { onLongClick(vehicle.id) },
                onToggleSelection = { onToggleSelection(vehicle.id) }
            )
        }
    }
}

/**
 * The Card for displaying a single vehicle.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VehicleCard(
    vehicle: Vehicle,
    index: Int,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
    onToggleSelection: () -> Unit
) {
    val cardColor = if (isSelected) {
        // Card shading when selected (darker or with overlay)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongTap
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox (Visible only in Selection Mode)
            AnimatedVisibility(visible = isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Vehicle Data
            Column(modifier = Modifier.weight(1f)) {
                // Sequential Number
                Text(
                    text = "#$index: ${vehicle.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Description (Make/Model/Odometer)
                Text(
                    text = "${vehicle.make} ${vehicle.model} - ${vehicle.initialOdometer} χλμ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
