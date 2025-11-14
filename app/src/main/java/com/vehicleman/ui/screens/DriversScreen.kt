package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.ui.viewmodel.DriversViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
    navController: NavController,
    viewModel: DriversViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_preferense_background_night) else painterResource(id = R.mipmap.img_preferense_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(text = "Manage Drivers") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                AddDriverForm(viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.drivers) { driverWithVehicles ->
                        val driverId = driverWithVehicles.driver.driverId
                        DriverCard(
                            driverName = driverWithVehicles.driver.name,
                            isExpanded = uiState.expandedDriverId == driverId,
                            onExpand = { viewModel.onDriverExpanded(driverId) },
                            onDelete = { viewModel.onDeleteDriver(driverId) },
                            assignedVehicles = driverWithVehicles.vehicles,
                            allVehicles = uiState.allVehicles,
                            onVehicleCheckedChange = { vehicleId, isChecked ->
                                viewModel.onVehicleCheckedChange(driverId, vehicleId, isChecked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddDriverForm(viewModel: DriversViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = uiState.newDriverName,
            onValueChange = viewModel::onNewDriverNameChange,
            label = { Text("New Driver Name") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = viewModel::onAddDriver) {
            Icon(Icons.Default.Add, contentDescription = "Add Driver")
        }
    }
}

@Composable
fun DriverCard(
    driverName: String,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onDelete: () -> Unit,
    assignedVehicles: List<Vehicle>,
    allVehicles: List<Vehicle>,
    onVehicleCheckedChange: (String, Boolean) -> Unit
) {
    val transparentLightGray = Color(0x80D3D3D3)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = transparentLightGray),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpand)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = driverName)
                Row {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Driver")
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    allVehicles.forEach { vehicle ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = assignedVehicles.any { it.id == vehicle.id },
                                onCheckedChange = { isChecked ->
                                    onVehicleCheckedChange(vehicle.id, isChecked)
                                }
                            )
                            Text(text = "${vehicle.make} ${vehicle.model} (${vehicle.plateNumber})")
                        }
                    }
                }
            }
        }
    }
}