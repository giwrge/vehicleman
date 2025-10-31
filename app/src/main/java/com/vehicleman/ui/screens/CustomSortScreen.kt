package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.vehicleman.ui.viewmodel.CustomSortViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSortScreen(
    navController: NavController,
    viewModel: CustomSortViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        viewModel.onMove(from.index, to.index)
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Custom Sort") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveCustomOrder()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply the padding here
                .reorderable(state)
                .detectReorderAfterLongPress(state) // This is the correct place
        ) {
            items(vehicles, { it.id }) { vehicle ->
                ReorderableItem(state, key = vehicle.id) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag handle",
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(text = vehicle.name)
                    }
                }
            }
        }
    }
}