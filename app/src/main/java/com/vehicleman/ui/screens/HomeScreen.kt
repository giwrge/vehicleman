package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R
import com.vehicleman.presentation.entries.EntriesPanelEvent
import com.vehicleman.presentation.entries.EntriesPanelViewModel
import com.vehicleman.ui.panels.EntriesPanel

/**
 * Η αρχική οθόνη που εμφανίζει τη λίστα των οχημάτων.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToVehicleForm: (vehicleId: String) -> Unit,
    onNavigateToEntryList: (vehicleId: String) -> Unit,
    viewModel: EntriesPanelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onLogoClick = { /* Go to Statistics/Settings */ },
                onPreferencesClick = { /* Go to Preferences */ }
            )
        },
        floatingActionButton = {
            // FAB με το ic_fab_add_vehicle.png
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigateToVehicleForm("new")
                    viewModel.onEvent(EntriesPanelEvent.AddNewVehicleClicked)
                },
                icon = {
                    Icon(
                        // Διόρθωση σφάλματος R.mipmap
                        painter = painterResource(id = R.mipmap.ic_fab_add_vehicle),
                        contentDescription = "Προσθήκη Οχήματος"
                    )
                },
                text = { Text("ΠΡΟΣΘΗΚΗ") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Background Image (2ον)
            Image(
                // Διόρθωση σφάλματος R.mipmap
                painter = painterResource(id = R.mipmap.img_home_background),
                contentDescription = "Background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )

            // Κύριο περιεχόμενο: Λίστα Οχημάτων
            EntriesPanel(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToEntryList = onNavigateToEntryList,
                onNavigateToEditVehicle = onNavigateToVehicleForm
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onLogoClick: () -> Unit,
    onPreferencesClick: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            // Logo (6ον)
            IconButton(onClick = onLogoClick) {
                Image(
                    // Διόρθωση σφάλματος R.mipmap
                    painter = painterResource(id = R.mipmap.ic_app_logo_main),
                    contentDescription = "Λογότυπο Εφαρμογής",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        actions = {
            // Preferences (8ον)
            IconButton(onClick = onPreferencesClick) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ρυθμίσεις / Preferences",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}