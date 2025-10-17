package com.vehicleman.ui.panels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelState
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem

/**
 * Εμφανίζει τη λίστα των οχημάτων στη HomeScreen.
 * Υποστηρίζει Tap, Double Tap, Long Tap με ενέργειες:
 * - Tap: RecordScreen
 * - Double Tap: AddEditRecordScreen
 * - Long Tap: Ενεργοποιεί AirflowCard με edit/delete
 */
@Composable
fun AddEditVehiclePanel(
    state: AddEditVehiclePanelState,
    onEvent: (AddEditVehiclePanelEvent) -> Unit,
    onNavigateToRecordScreen: (String) -> Unit,
    onNavigateToAddEditRecord: (String) -> Unit,
    onNavigateToEditVehicle: (String) -> Unit
) {
    var airflowVehicleId by remember { mutableStateOf<String?>(null) }

    // Αν αλλάξει η λίστα, μηδενίζουμε το airflow
    LaunchedEffect(state.vehicles.size) {
        if (state.vehicles.none { it.id == airflowVehicleId }) airflowVehicleId = null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
        ) {
            VehicleList(
                state = state,
                airflowVehicleId = airflowVehicleId,
                onTap = { id ->
                    airflowVehicleId = null
                    onNavigateToRecordScreen(id)
                },
                onDoubleTap = { id ->
                    airflowVehicleId = null
                    onNavigateToAddEditRecord(id)
                },
                onLongTap = { id ->
                    airflowVehicleId = if (airflowVehicleId == id) null else id
                },
                onEditClick = { id ->
                    airflowVehicleId = null
                    onNavigateToEditVehicle(id)
                },
                onDeleteClick = { id ->
                    onEvent(AddEditVehiclePanelEvent.DeleteVehicleById(id))
                    airflowVehicleId = null
                }
            )
        }
    }
}

@Composable
private fun VehicleList(
    state: AddEditVehiclePanelState,
    airflowVehicleId: String?,
    onTap: (String) -> Unit,
    onDoubleTap: (String) -> Unit,
    onLongTap: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        when {
            state.isLoading -> item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> item {
                Text(
                    text = "Σφάλμα: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            state.vehicles.isEmpty() && !state.isLoading -> item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Δεν υπάρχουν καταχωρημένα οχήματα.", style = MaterialTheme.typography.titleMedium)
                }
            }
            else -> {
                items(state.vehicles, key = { it.id }) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        isAirflow = vehicle.id == airflowVehicleId,
                        onTap = onTap,
                        onDoubleTap = onDoubleTap,
                        onLongTap = onLongTap,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun VehicleCard(
    vehicle: VehicleDisplayItem,
    isAirflow: Boolean,
    onTap: (String) -> Unit,
    onDoubleTap: (String) -> Unit,
    onLongTap: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val shadowElevation = if (isAirflow) 8.dp else 2.dp

    var lastTapTime by remember { mutableStateOf(0L) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (isAirflow) 1f else 0f)
            .shadow(shadowElevation, RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTime < 300) { // Double Tap
                        onDoubleTap(vehicle.id)
                    } else {
                        onTap(vehicle.id)
                    }
                    lastTapTime = now
                },
                onLongClick = { onLongTap(vehicle.id) }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAirflow)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(vehicle.makeModel, style = MaterialTheme.typography.bodyMedium)
                Text("Πινακίδα: ${vehicle.licensePlate}", style = MaterialTheme.typography.labelMedium)
            }
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Λίστα εγγραφών")
        }
    }

    // Airflow Buttons
    if (isAirflow) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-16).dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            FlowButton(Icons.Default.Delete, "Διαγραφή", onClick = { onDeleteClick(vehicle.id) })
            Spacer(Modifier.width(8.dp))
            FlowButton(Icons.Default.Build, "Επεξεργασία", onClick = { onEditClick(vehicle.id) })
        }
    }
}

@Composable
private fun FlowButton(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray.copy(alpha = 0.8f),
            contentColor = Color.DarkGray
        )
    ) {
        Icon(icon, contentDescription = desc, modifier = Modifier.size(20.dp))
    }
}
