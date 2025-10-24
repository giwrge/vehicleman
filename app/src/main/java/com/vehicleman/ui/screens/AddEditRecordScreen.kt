package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    navController: NavController,
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold {
        padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnTitleChanged(it)) },
                label = { Text("Title") }
            )
            TextField(
                value = state.odometer,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnOdometerChanged(it)) },
                label = { Text("Odometer") }
            )
            TextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnDescriptionChanged(it)) },
                label = { Text("Description") }
            )
            TextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnAmountChanged(it)) },
                label = { Text("Amount") }
            )
            Button(onClick = {
                viewModel.onEvent(AddEditRecordEvent.OnSaveRecord)
                navController.popBackStack()
            }) {
                Text("Save")
            }
        }
    }
}
