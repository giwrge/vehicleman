package com.vehicleman.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.Driver
import com.vehicleman.presentation.addeditvehicle.VehicleDisplayItem
import com.vehicleman.presentation.statistics.StatisticsEvent
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
        navigationState.navigateToDetailedAnalysis?.let { route ->
            navController.navigate(route)
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
                    title = { Text("Statistics", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            var totalDragAmount by remember { mutableStateOf(0f) }
            
            Row(
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
                                if (totalDragAmount > 30) { // Swipe Right to go back
                                    val route = when (fromScreen) {
                                        NavDestinations.HOME_IDENTIFIER -> NavDestinations.HOME_ROUTE
                                        NavDestinations.RECORD_IDENTIFIER -> NavDestinations.entryListRoute(fromId ?: "")
                                        else -> NavDestinations.HOME_ROUTE
                                    }
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    // Left Column: Vehicles (65%)
                    Column(modifier = Modifier.weight(0.65f)) {
                        Text(
                            "Οχήματα",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.vehicles) { vehicle ->
                                StatisticsVehicleCard(
                                    vehicle = vehicle,
                                    onClick = { viewModel.onEvent(StatisticsEvent.OnVehicleClick(vehicle)) }
                                )
                            }
                        }
                    }

                    // Right Column: Users (35%)
                    Column(modifier = Modifier.weight(0.35f)) {
                        Text(
                            "Χρήστες",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(start = 8.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.drivers) { driver ->
                                DriverStatisticsCard(
                                    driver = driver,
                                    onClick = { viewModel.onEvent(StatisticsEvent.OnDriverClick(driver)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsVehicleCard(
    vehicle: VehicleDisplayItem,
    onClick: () -> Unit
) {
    val glassColor = Color.White.copy(alpha = 0.15f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = glassColor),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(vehicle.name, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            Text(vehicle.licensePlate, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            Text(
                vehicle.fuelTypes.joinToString(", "),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun DriverStatisticsCard(
    driver: Driver,
    onClick: () -> Unit
) {
    val glassColor = Color.White.copy(alpha = 0.15f)
    val isMainUser = driver.driverId == "main_user"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMainUser) Color.Yellow.copy(alpha = 0.2f) else glassColor
        ),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = driver.name,
                fontWeight = if (isMainUser) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isMainUser) Color.Yellow else Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
