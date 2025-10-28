package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.ui.viewmodel.SubDriverPermissionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubDriverPermissionsScreen(navController: NavController, subDriverId: String, viewModel: SubDriverPermissionsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Permissions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.savePermissions()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Set permissions for Sub-Driver: $subDriverId")
            
            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected = (uiState.selectedType == SubDriverType.FULL), onClick = { viewModel.onPermissionTypeChange(SubDriverType.FULL) })
                        .padding(vertical = 8.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (uiState.selectedType == SubDriverType.FULL), onClick = { viewModel.onPermissionTypeChange(SubDriverType.FULL) })
                    Text("Full Access", modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected = (uiState.selectedType == SubDriverType.SINGLE), onClick = { viewModel.onPermissionTypeChange(SubDriverType.SINGLE) })
                        .padding(vertical = 8.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (uiState.selectedType == SubDriverType.SINGLE), onClick = { viewModel.onPermissionTypeChange(SubDriverType.SINGLE) })
                    Text("Single Vehicle Access", modifier = Modifier.padding(start = 8.dp))
                }
            }

            AnimatedVisibility(visible = uiState.selectedType == SubDriverType.SINGLE) {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    items(uiState.allVehicles) { vehicle ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.assignedVehicleIds.contains(vehicle.id),
                                onCheckedChange = { isChecked -> viewModel.onVehicleCheckedChange(vehicle.id, isChecked) }
                            )
                            Text("${vehicle.make} ${vehicle.model}")
                        }
                    }
                }
            }
        }
    }
}