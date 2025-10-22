package com.vehicleman.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    onNavigateToAddEditVehicle: (String?) -> Unit,
    onNavigateToRecord: (String) -> Unit
) {
    val vehicles by homeViewModel.vehicles.collectAsState()
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { HomeTopAppBar() },
            floatingActionButton = {
                Surface(
                    onClick = { onNavigateToAddEditVehicle(null) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .alpha(0.8f) // Apply 80% transparency to the entire FAB
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_fab_add_vehicle),
                        contentDescription = "Προσθήκη Οχήματος",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        ) { padding ->
            // Apply the tap gesture for deselection here
            Box(modifier = Modifier.fillMaxSize().padding(padding).pointerInput(selectedVehicleId) {
                if (selectedVehicleId != null) {
                    detectTapGestures(onTap = { selectedVehicleId = null })
                }
            }) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(vehicles, key = { it.id }) { vehicle ->
                        VehicleListItem(
                            vehicle = vehicle,
                            isSelected = vehicle.id == selectedVehicleId,
                            onTap = { onNavigateToRecord(vehicle.id) },
                            onLongTap = { selectedVehicleId = vehicle.id },
                            onEdit = { onNavigateToAddEditVehicle(vehicle.id) },
                            onDelete = { showDeleteDialog = vehicle.id }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { vehicleId ->
        val vehicleToDelete = vehicles.find { it.id == vehicleId }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    homeViewModel.onEvent(AddEditVehiclePanelEvent.DeleteVehicleById(vehicleId))
                    showDeleteDialog = null
                    selectedVehicleId = null
                }) { Text("Διαγραφή") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Ακύρωση") } },
            title = { Text("Επιβεβαίωση Διαγραφής") },
            text = { Text("Να διαγραφεί το όχημα ${vehicleToDelete?.name ?: ""};") }
        )
    }
}

@Composable
fun VehicleListItem(
    vehicle: VehicleDisplayItem,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        if (isSelected) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_edit_tools_v2),
                    contentDescription = "Επεξεργασία",
                    modifier = Modifier.size(40.dp).clickable(onClick = onEdit)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_delete_bin_v2),
                    contentDescription = "Διαγραφή",
                    modifier = Modifier.size(40.dp).clickable(onClick = onDelete)
                )
            }
        }
        VehicleCard(
            vehicle = vehicle,
            isSelected = isSelected,
            onTap = onTap,
            onLongTap = onLongTap,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VehicleCard(
    vehicle: VehicleDisplayItem,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        else MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
        label = "background-color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onClick = onTap, onLongClick = onLongTap)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(vehicle.name, style = MaterialTheme.typography.titleMedium, color = Color.Black)
                Text("Πινακίδα: ${vehicle.licensePlate}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall, color = Color.Black)
                Text("Καύσιμο: ${vehicle.fuelType}", style = MaterialTheme.typography.bodySmall, color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar() {
    TopAppBar(
        title = { Text("") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}
