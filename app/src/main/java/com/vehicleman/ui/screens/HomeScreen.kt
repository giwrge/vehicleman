package com.vehicleman.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    onNavigateToAddEditVehicle: (String?) -> Unit,
    onNavigateToRecord: (String) -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToPreferences: () -> Unit
) {
    val vehicles by homeViewModel.vehicles.collectAsState()
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 150f

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HomeTopAppBar(
                    onLogoClick = onNavigateToStatistics,
                    onPreferencesClick = onNavigateToPreferences
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onNavigateToAddEditVehicle(null) },
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_fab_add_vehicle),
                        contentDescription = "Προσθήκη Οχήματος",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > swipeThreshold -> onNavigateToPreferences()
                                    offsetX < -swipeThreshold -> onNavigateToStatistics()
                                }
                                offsetX = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX += dragAmount
                            }
                        )
                    }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vehicles, key = { it.id }) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            isSelected = vehicle.id == selectedVehicleId,
                            onTap = {
                                if (selectedVehicleId == vehicle.id) {
                                    selectedVehicleId = null
                                } else {
                                    onNavigateToRecord(vehicle.id)
                                }
                            },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VehicleCard(
    vehicle: VehicleDisplayItem,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.surface,
        label = "background-color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .combinedClickable(
                    onClick = onTap,
                    onLongClick = onLongTap
                )
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(vehicle.name, style = MaterialTheme.typography.titleMedium)
                Text("Πινακίδα: ${vehicle.licensePlate}", style = MaterialTheme.typography.bodyMedium)
                Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall)
            }

            if (isSelected) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Επεξεργασία")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Διαγραφή", tint = Color.Red)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onLogoClick: () -> Unit,
    onPreferencesClick: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onLogoClick) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_app_logo_main),
                    contentDescription = "Λογότυπο",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onPreferencesClick) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ρυθμίσεις",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
