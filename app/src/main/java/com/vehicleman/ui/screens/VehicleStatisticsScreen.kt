package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.presentation.vehiclestatistics.VehicleStatisticsEvent
import com.vehicleman.presentation.vehiclestatistics.VehicleStatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleStatisticsScreen(
    navController: NavController,
    viewModel: VehicleStatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.vehicleName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(VehicleStatisticsEvent.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.error!!)
            }
        } else if (state.stats != null) {
            val stats = state.stats!!
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                StatRow("Average Consumption:", "${String.format("%.2f", stats.averageConsumptionLitersPer100km)} L/100km")
                StatRow("Average Cost:", "${String.format("%.2f", stats.averageCostPer100km)} /100km")
                Spacer(modifier = Modifier.height(16.dp))
                StatRow("Estimated Monthly Km:", "${String.format("%.0f", stats.estimatedMonthlyKm)} km")
                StatRow("Estimated Yearly Km:", "${String.format("%.0f", stats.estimatedYearlyKm)} km")
                Spacer(modifier = Modifier.height(16.dp))
                StatRow("Estimated Monthly Cost:", "${String.format("%.2f", stats.estimatedMonthlyCost)}")
                StatRow("Estimated Yearly Cost:", "${String.format("%.2f", stats.estimatedYearlyCost)}")
                Spacer(modifier = Modifier.height(16.dp))
                StatRow("Total Fuel Cost:", "${String.format("%.2f", stats.totalFuelCost)}")
                StatRow("Total Service Cost:", "${String.format("%.2f", stats.totalServiceCost)}")
                StatRow("Total Insurance Cost:", "${String.format("%.2f", stats.totalInsuranceCost)}")
                StatRow("Total Tax Cost:", "${String.format("%.2f", stats.totalTaxCost)}")
                StatRow("Total Other Expenses:", "${String.format("%.2f", stats.totalOtherExpenses)}")
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}
