package com.vehicleman.ui.panels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Build // Γαλλικό κλειδί
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vehicleman.presentation.entries.EntriesPanelEvent
import com.vehicleman.presentation.entries.EntriesPanelState
import com.vehicleman.presentation.entries.VehicleDisplayItem

/**
 * Displays the list of vehicles and handles the Airflow Card logic.
 *
 * @param onNavigateToEntryList Callback for single tap (go to Entries list).
 * @param onNavigateToEditVehicle Callback for editing a vehicle (from Airflow Card).
 */
@Composable
fun EntriesPanel(
    state: EntriesPanelState,
    onEvent: (EntriesPanelEvent) -> Unit,
    onNavigateToEntryList: (String) -> Unit,
    onNavigateToEditVehicle: (String) -> Unit
) {
    // State για την εμφάνιση της Airflow Card
    var airflowVehicleId by remember { mutableStateOf<String?>(null) }

    // Όταν η λίστα αλλάζει ή γίνεται διαγραφή, κλείνουμε την Airflow Card
    LaunchedEffect(state.vehicles.size) {
        if (state.vehicles.none { it.id == airflowVehicleId }) {
            airflowVehicleId = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        // Προσομοίωση background εικόνας (Επειδή λείπει το asset)
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)) // Λευκό φόντο
        ) {
            // Background Image Placeholder (Κάτω από τη λίστα)
            // Στην πραγματική εφαρμογή, θα χρησιμοποιούσατε AsyncImage(model = R.drawable.img_home_background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFFE0E0E0).copy(alpha = 0.5f)) // Ανοιχτό γκρι placeholder
            ) {
                Text(
                    "Background Placeholder",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }

            VehicleList(
                state = state,
                airflowVehicleId = airflowVehicleId,
                onSingleTap = { vehicleId ->
                    if (airflowVehicleId == vehicleId) {
                        // Αν η Airflow Card είναι ανοιχτή, την κλείνουμε
                        airflowVehicleId = null
                    } else if (airflowVehicleId == null) {
                        // Κανονικό Tap
                        onNavigateToEntryList(vehicleId)
                    }
                },
                onLongTap = { vehicleId ->
                    // Ενεργοποίηση Airflow Card
                    airflowVehicleId = vehicleId
                },
                onEditClick = { id ->
                    airflowVehicleId = null
                    onNavigateToEditVehicle(id)
                },
                onDeleteClick = { id ->
                    // Εμφάνιση Modal διαγραφής
                    onEvent(EntriesPanelEvent.DeleteVehicleById(id))
                    airflowVehicleId = null
                }
            )
        }
    }
}

@Composable
private fun VehicleList(
    state: EntriesPanelState,
    airflowVehicleId: String?,
    onSingleTap: (String) -> Unit,
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
                    text = "Error: ${state.error}",
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
                items(state.vehicles, key = { it.id }) { item ->
                    VehicleAirflowItem(
                        vehicle = item,
                        isAirflow = item.id == airflowVehicleId,
                        onSingleTap = onSingleTap,
                        onLongTap = onLongTap,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun VehicleAirflowItem(
    vehicle: VehicleDisplayItem,
    isAirflow: Boolean,
    onSingleTap: (String) -> Unit,
    onLongTap: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val zIndex = if (isAirflow) 1f else 0f
    val shadowElevation = if (isAirflow) 8.dp else 2.dp
    val airflowModifier = if (isAirflow) Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp) else Modifier

    // Main Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(zIndex) // Ανυψώνει την κάρτα πάνω από τις άλλες
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(16.dp)
            )
            .then(airflowModifier)
            .combinedClickable(
                onClick = { onSingleTap(vehicle.id) },
                onLongClick = { onLongTap(vehicle.id) }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAirflow) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
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
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Λίστα Συμβάντων",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Flow Buttons (Εμφανίζονται μόνο στην Airflow Card)
    if (isAirflow) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-16).dp) // Μετακινεί τα κουμπιά πάνω από την κάρτα
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            FlowButton(
                icon = Icons.Default.Delete,
                onClick = { onDeleteClick(vehicle.id) },
                contentDescription = "Διαγραφή Οχήματος"
            )
            Spacer(modifier = Modifier.width(8.dp))
            FlowButton(
                icon = Icons.Default.Build,
                onClick = { onEditClick(vehicle.id) },
                contentDescription = "Τροποποίηση Οχήματος"
            )
        }
    }
}

@Composable
private fun FlowButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String
) {
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
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(20.dp))
    }
}