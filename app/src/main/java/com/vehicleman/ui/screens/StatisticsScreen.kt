package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.presentation.statistics.StatisticsViewModel
import com.vehicleman.ui.navigation.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show driver selection tabs only if the user is NOT a SingleSubDriver
            if (!state.isSingleSubDriver) {
                val allDrivers = listOf(null) + state.drivers // Add "Main/All" option
                val selectedTabIndex = allDrivers.indexOfFirst { it?.driverId == state.selectedDriverId }
                
                ScrollableTabRow(selectedTabIndex = selectedTabIndex.coerceAtLeast(0)) {
                    allDrivers.forEachIndexed { index, driver ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { viewModel.onDriverSelected(driver?.driverId) },
                            text = { Text(driver?.name ?: "Main") }
                        )
                    }
                }
            }

            // Vehicle list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                items(state.vehicles) { vehicle ->
                    VehicleStatsCard(vehicle = vehicle, onClick = {
                        navController.navigate(NavDestinations.statisticVehicleRoute(vehicle.id))
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleStatsCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${vehicle.make} ${vehicle.model}", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text(text = "Plate: ${vehicle.plateNumber}")
            // TODO: Add some basic stats here later if needed
        }
    }
}