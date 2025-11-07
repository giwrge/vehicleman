package com.vehicleman.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.Driver
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.presentation.statistics.StatisticsEvent
import com.vehicleman.presentation.statistics.StatisticsViewModel
import com.vehicleman.ui.navigation.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    fromScreen: String?,
    fromId: String?,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToDriverStatistics) {
        state.navigateToDriverStatistics?.let {
            navController.navigate("DriverStatisticsScreen/$it")
            viewModel.onEvent(StatisticsEvent.NavigationHandled)
        }
    }

    LaunchedEffect(state.navigateToVehicleStatistics) {
        state.navigateToVehicleStatistics?.let {
            navController.navigate("${NavDestinations.STATISTIC_VEHICLE_ROUTE}/$it")
            viewModel.onEvent(StatisticsEvent.NavigationHandled)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        
        val baseModifier = Modifier.padding(paddingValues).fillMaxSize()

        when {
            state.isLoading -> {
                Box(modifier = baseModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.drivers.isEmpty() && state.vehicles.isEmpty() -> {
                Box(
                    modifier = baseModifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_statistics_data),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                var totalDragAmount by remember { mutableStateOf(0f) }
                Column(
                    modifier = baseModifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { totalDragAmount = 0f },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDragAmount += dragAmount
                            },
                            onDragEnd = {
                                if (totalDragAmount > 30) { // Swipe Right
                                    val route = when (fromScreen) {
                                        NavDestinations.HOME_IDENTIFIER -> NavDestinations.HOME_ROUTE
                                        NavDestinations.RECORD_IDENTIFIER -> NavDestinations.entryListRoute(fromId ?: "")
                                        else -> NavDestinations.HOME_ROUTE // Default fallback
                                    }
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
                ) {
                    if (state.drivers.isNotEmpty()) {
                        Text("Drivers", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.drivers) {
                                DriverCard(driver = it, onClick = { viewModel.onEvent(StatisticsEvent.OnDriverClick(it)) })
                            }
                        }
                    }
                    if (state.vehicles.isNotEmpty()) {
                        Text("Vehicles", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.vehicles) {
                                VehicleCard(vehicle = it, onClick = { viewModel.onEvent(StatisticsEvent.OnVehicleClick(it)) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverCard(driver: Driver, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = driver.name, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(text = vehicle.name, modifier = Modifier.padding(16.dp))
    }
}
