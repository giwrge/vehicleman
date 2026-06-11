package com.vehicleman.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    val contentColor = if (isNightMode) Color.White else Color.Black

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
                    title = { Text("Statistics", color = contentColor) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = contentColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            var totalDragAmount by remember { mutableFloatStateOf(0f) }
            
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
                        CircularProgressIndicator(color = contentColor)
                    }
                } else {
                    // Left Column: Vehicles (65%)
                    Column(modifier = Modifier.weight(0.65f)) {
                        Text(
                            "Οχήματα",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = contentColor,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.vehicles) { vehicle ->
                                StatisticsVehicleCard(
                                    vehicle = vehicle,
                                    isNightMode = isNightMode,
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
                            color = contentColor,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(start = 8.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.drivers) { driver ->
                                DriverStatisticsCard(
                                    driver = driver,
                                    isNightMode = isNightMode,
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
    isNightMode: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isNightMode) Color.White else Color.Black
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF4CAF50).copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(vehicle.name, fontWeight = FontWeight.Bold, color = contentColor, maxLines = 1)
                Text(vehicle.licensePlate, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
                Text(vehicle.odometerText, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
                Text(
                    vehicle.fuelTypes.joinToString(", "),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DriverStatisticsCard(
    driver: Driver,
    isNightMode: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isNightMode) Color.White else Color.Black
    val isMainUser = driver.driverId == "main_user"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2196F3).copy(alpha = 0.4f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = driver.name,
                fontWeight = if (isMainUser) FontWeight.ExtraBold else FontWeight.Medium,
                color = contentColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
