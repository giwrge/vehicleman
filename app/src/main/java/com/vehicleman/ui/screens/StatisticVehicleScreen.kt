package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.presentation.statisticvehicle.StatisticVehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticVehicleScreen(
    navController: NavController,
    viewModel: StatisticVehicleViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold {
        padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "Total Consumption: ${state.totalConsumption}")
            state.consumptionPerFuelType.forEach {
                (fuelType, consumption) ->
                Text(text = "$fuelType: $consumption")
            }
        }
    }
}
