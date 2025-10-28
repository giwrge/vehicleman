package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartData
import com.vehicleman.domain.util.CalculatedStats
import com.vehicleman.domain.util.FuelStats
import com.vehicleman.presentation.vehicle.StatisticVehicleViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticVehicleScreen(
    navController: NavController,
    viewModel: StatisticVehicleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.vehicle?.name ?: "Vehicle Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OverallConsumptionCard(stats = state.stats)
                FuelTypeSpecificCard(stats = state.stats.statsByFuelType)
                CostDistributionCard(stats = state.stats)
            }
        }
    }
}

@Composable
fun OverallConsumptionCard(stats: CalculatedStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Overall Performance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            StatRow("Avg. Consumption:", "${String.format("%.2f", stats.averageConsumptionLitersPer100km)} L/100km")
            StatRow("Avg. Cost:", "${String.format("%.2f", stats.averageCostPer100km)} €/100km")
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Estimations", style = MaterialTheme.typography.titleMedium)
            StatRow("Monthly Km (est.):", "${String.format("%.0f", stats.estimatedMonthlyKm)} km")
            StatRow("Yearly Km (est.):", "${String.format("%.0f", stats.estimatedYearlyKm)} km")
            StatRow("Monthly Cost (est.):", "${String.format("%.2f", stats.estimatedMonthlyCost)} €")
            StatRow("Yearly Cost (est.):", "${String.format("%.2f", stats.estimatedYearlyCost)} €")
        }
    }
}

@Composable
fun FuelTypeSpecificCard(stats: List<FuelStats>) {
    if (stats.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Stats per Fuel Type", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                stats.forEach {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it.fuelType, style = MaterialTheme.typography.titleMedium)
                    StatRow("Avg. Consumption:", "${String.format("%.2f", it.averageConsumption)} L/100km")
                    StatRow("Avg. Cost:", "${String.format("%.2f", it.averageCostPer100km)} €/100km")
                }
            }
        }
    }
}

@Composable
fun CostDistributionCard(stats: CalculatedStats) {
    val slices = listOfNotNull(
        if (stats.totalFuelCost > 0) PieChartData.Slice("Fuel", stats.totalFuelCost.toFloat(), randomColor()) else null,
        if (stats.totalServiceCost > 0) PieChartData.Slice("Service", stats.totalServiceCost.toFloat(), randomColor()) else null,
        if (stats.totalInsuranceCost > 0) PieChartData.Slice("Insurance", stats.totalInsuranceCost.toFloat(), randomColor()) else null,
        if (stats.totalTaxCost > 0) PieChartData.Slice("Taxes", stats.totalTaxCost.toFloat(), randomColor()) else null,
        if (stats.totalOtherExpenses > 0) PieChartData.Slice("Other", stats.totalOtherExpenses.toFloat(), randomColor()) else null
    )

    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cost Distribution", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            if (pieChartData.slices.isEmpty()) {
                Text("No cost data available.")
            } else {
                PieChart(modifier = Modifier.size(150.dp), pieChartData = pieChartData)
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}

fun randomColor(): Color {
    return Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
}