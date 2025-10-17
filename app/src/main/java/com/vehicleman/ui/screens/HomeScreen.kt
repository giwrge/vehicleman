package com.vehicleman.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R
import com.vehicleman.presentation.addeditvehicle.AddEditVehiclePanelEvent
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.ui.viewmodel.HomeViewModel
import kotlin.math.abs

/**
 * 🏠 HomeScreen
 * - Προβολή λίστας οχημάτων
 * - Swipe navigation: Preferences ↔ Home ↔ Statistics
 * - Tap → RecordScreen
 * - Double Tap → AddEditRecordScreen
 * - Long Tap → εμφάνιση μενού Edit/Delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecord: (String) -> Unit,
    onNavigateToAddEditRecord: (String) -> Unit,
    onNavigateToAddEditVehicle: (String) -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var selectedVehicle by remember { mutableStateOf<VehicleDisplayItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // swipe detection
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 150f

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onLogoClick = onNavigateToStatistics,
                onPreferencesClick = onNavigateToPreferences
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToAddEditVehicle("new") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_fab_add_vehicle),
                        contentDescription = "Προσθήκη Οχήματος"
                    )
                },
                text = { Text("ΠΡΟΣΘΗΚΗ") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
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
            Image(
                painter = painterResource(id = R.mipmap.img_home_background),
                contentDescription = "Background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Σφάλμα: ${state.error}") }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.vehicles) { vehicle ->
                            VehicleCard(
                                vehicle = vehicle,
                                onTap = { onNavigateToRecord(vehicle.id) },
                                onDoubleTap = { onNavigateToAddEditRecord(vehicle.id) },
                                onLongTap = { selectedVehicle = vehicle },
                                isDimmed = selectedVehicle != null && selectedVehicle != vehicle
                            )
                        }
                    }
                }
            }

            // Long-tap menu
            selectedVehicle?.let { vehicle ->
                VehicleActionMenu(
                    vehicle = vehicle,
                    onEdit = {
                        selectedVehicle = null
                        onNavigateToAddEditVehicle(vehicle.id)
                    },
                    onDelete = { showDeleteDialog = true },
                    onDismiss = { selectedVehicle = null }
                )
            }

            if (showDeleteDialog && selectedVehicle != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(
                                AddEditVehiclePanelEvent.DeleteVehicleById(selectedVehicle!!.id)
                            )
                            showDeleteDialog = false
                            selectedVehicle = null
                        }) { Text("Διαγραφή") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Ακύρωση") }
                    },
                    title = { Text("Επιβεβαίωση Διαγραφής") },
                    text = { Text("Να διαγραφεί το όχημα ${selectedVehicle?.name};") }
                )
            }
        }
    }
}

/**
 * VehicleCard - Single vehicle item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleCard(
    vehicle: VehicleDisplayItem,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongTap: () -> Unit,
    isDimmed: Boolean
) {
    var lastClickTime by remember { mutableStateOf(0L) }
    val doubleClickThreshold = 300L
    val backgroundColor by animateColorAsState(
        if (isDimmed) Color.LightGray.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surface,
        label = ""
    )

    Card(
        onClick = {
            val current = System.currentTimeMillis()
            if (current - lastClickTime < doubleClickThreshold) onDoubleTap()
            else onTap()
            lastClickTime = current
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (abs(dragAmount) < 5f) onLongTap()
                    }
                }
        ) {
            Text(vehicle.name, style = MaterialTheme.typography.titleMedium)
            Text("Πινακίδα: ${vehicle.licensePlate}")
            Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Μενού ενεργειών για Long Tap
 */
@Composable
fun VehicleActionMenu(
    vehicle: VehicleDisplayItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Επιλογές Οχήματος", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Επεξεργασία")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Διαγραφή", tint = Color.Red)
                    }
                }
                TextButton(onClick = onDismiss) { Text("Άκυρο") }
            }
        }
    }
}

/**
 * TopAppBar (Λογότυπο + Ρυθμίσεις)
 */
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
