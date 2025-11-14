package com.vehicleman.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.Driver
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.presentation.statistics.NavigationState
import com.vehicleman.presentation.statistics.StatisticsEvent
import com.vehicleman.presentation.statistics.StatisticsState
import com.vehicleman.presentation.statistics.StatisticsViewModel
import com.vehicleman.ui.navigation.NavDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    fromScreen: String?,
    fromId: String?,
    viewModel: StatisticsViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigationState by viewModel.navigationState.collectAsStateWithLifecycle()

    LaunchedEffect(navigationState) {
        navigationState.navigateToDriverStatistics?.let {
            navController.navigate("DriverStatisticsScreen/$it")
            viewModel.onEvent(StatisticsEvent.NavigationHandled)
        }
        navigationState.navigateToVehicleStatistics?.let {
            navController.navigate("VehicleStatisticsScreen/$it")
            viewModel.onEvent(StatisticsEvent.NavigationHandled)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_statistic_background_night) else painterResource(id = R.mipmap.img_statistic_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
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
            var totalDragAmount by remember { mutableStateOf(0f) }
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .pointerInput(Unit) {
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
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    // Drivers Section
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.drivers) { driver ->
                            DriverCard(driver = driver, onClick = { viewModel.onEvent(StatisticsEvent.OnDriverClick(driver)) })
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

                    // Vehicles Section
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.vehicles) { vehicle ->
                            StatisticsVehicleCard(
                                vehicle = vehicle,
                                onClick = { viewModel.onEvent(StatisticsEvent.OnVehicleClick(vehicle)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverCard(driver: Driver, onClick: () -> Unit) {
    val transparentLightGray = Color(0x80D3D3D3)
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = transparentLightGray),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = driver.name,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticsVehicleCard(
    vehicle: VehicleDisplayItem,
    onClick: () -> Unit
) {
    val transparentLightGray = Color(0x80D3D3D3)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = transparentLightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onClick = onClick)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(vehicle.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Πινακίδα: ${vehicle.licensePlate}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                Text("Καύσιμο: ${vehicle.fuelTypes.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
