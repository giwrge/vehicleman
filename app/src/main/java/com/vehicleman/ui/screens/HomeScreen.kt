package com.vehicleman.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    onNavigateToAddEditVehicle: (String?) -> Unit,
    onNavigateToRecord: (String) -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToProMode: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    isNightMode: Boolean
) {
    val vehicles by homeViewModel.vehicles.collectAsState()
    val user by homeViewModel.user.collectAsState()
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    val isSingleSubDriver = user.twinAppRole == TwinAppRole.SUB_DRIVER && user.subDriverType == SubDriverType.SINGLE

    var totalDragAmount by remember { mutableStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragStart = { totalDragAmount = 0f },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    totalDragAmount += dragAmount
                },
                onDragEnd = {
                    if (totalDragAmount < -30) { // Swipe Left
                        onNavigateToStatistics()
                    } else if (totalDragAmount > 30) { // Swipe Right
                        onNavigateToPreferences()
                    }
                }
            )
        }
    ) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_home_background_night) else painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { HomeTopAppBar(onNavigateToPreferences, onNavigateToProMode, onNavigateToSignUp) },
            floatingActionButton = {
                if (!isSingleSubDriver) { // Hide FAB for SingleSubDrivers
                    Surface(
                        onClick = { onNavigateToAddEditVehicle(null) },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .width(90.dp)
                            .height(70.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_fab_add_vehicle),
                            contentDescription = "Προσθήκη Οχήματος",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        ) { padding ->
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
                            onDoubleTap = { navController.navigate(NavDestinations.addEditEntryRoute(vehicle.id, null)) },
                            onLongTap = { selectedVehicleId = vehicle.id },
                            onEdit = { 
                                if (!isSingleSubDriver) { // Prevent editing for SingleSubDrivers
                                    onNavigateToAddEditVehicle(vehicle.id) 
                                }
                            },
                            onDelete = { showDeleteDialog = vehicle.id },
                            isSingleSubDriver = isSingleSubDriver
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
    onDoubleTap: () -> Unit,
    onLongTap: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isSingleSubDriver: Boolean
) {
    Column(horizontalAlignment = Alignment.Start) {
        if (isSelected && !isSingleSubDriver) { // Hide edit/delete for SingleSubDrivers
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_wrench_edit),
                    contentDescription = "Επεξεργασία",
                    modifier = Modifier.size(40.dp).clickable(onClick = onEdit)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_delete),
                    contentDescription = "Διαγραφή",
                    modifier = Modifier.size(40.dp).clickable(onClick = onDelete)
                )
            }
        }
        VehicleCard(
            vehicle = vehicle,
            isSelected = isSelected,
            onTap = onTap,
            onDoubleTap = onDoubleTap,
            onLongTap = if (isSingleSubDriver) {{}} else onLongTap, // Disable long tap for SingleSubDrivers
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VehicleCard(
    vehicle: VehicleDisplayItem,
    isSelected: Boolean,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongTap: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFC8E6C9).copy(alpha = 0.65f) else Color.White.copy(alpha = 0.65f),
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
                .combinedClickable(onClick = onTap, onLongClick = onLongTap, onDoubleClick = onDoubleTap)
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
                Text("Καύσιμο: ${vehicle.fuelTypes}", style = MaterialTheme.typography.bodySmall, color = Color.Black) // Corrected
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onNavigateToPreferences: () -> Unit,
    onNavigateToProMode: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        actions = {
            Image(
                painter = painterResource(id = R.mipmap.ic_sing_up),
                contentDescription = "Sign Up",
                modifier = Modifier.size(40.dp).padding(end = 8.dp).clickable(onClick = onNavigateToSignUp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_promode_vip),
                contentDescription = "Pro Mode",
                modifier = Modifier.size(40.dp).padding(end = 8.dp).clickable(onClick = onNavigateToProMode)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_settings),
                contentDescription = "Settings",
                modifier = Modifier.size(40.dp).padding(end = 8.dp).clickable(onClick = onNavigateToPreferences)
            )
        }
    )
}
