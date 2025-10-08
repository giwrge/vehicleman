package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings // Για Preferences
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R // Χρειάζεται για τα assets
import com.vehicleman.presentation.entries.EntriesPanelEvent
import com.vehicleman.presentation.entries.EntriesPanelViewModel
import com.vehicleman.ui.panels.EntriesPanel

/**
 * Η αρχική οθόνη που εμφανίζει τη λίστα των οχημάτων.
 *
 * @param onNavigateToVehicleForm Callback για πλοήγηση στη φόρμα προσθήκης/επεξεργασίας.
 * @param onNavigateToEntryList Callback για πλοήγηση στη λίστα συμβάντων του οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToVehicleForm: (vehicleId: String) -> Unit,
    onNavigateToEntryList: (vehicleId: String) -> Unit, // ΝΕΟ: Για Tap στην καρτέλα
    viewModel: EntriesPanelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onLogoClick = { /* Go to Statistics */ },
                onPreferencesClick = { /* Go to Preferences */ }
            )
        },
        floatingActionButton = {
            // FAB με το δικό σας εικονίδιο
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigateToVehicleForm("new")
                    viewModel.onEvent(EntriesPanelEvent.AddNewVehicleClicked)
                },
                // Προσομοίωση FAB icon
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fab_add_vehicle), // ΥΠΟΘΕΤΩ R.drawable.ic_fab_add_vehicle
                        contentDescription = "Προσθήκη Οχήματος"
                    )
                },
                text = { Text("ΠΡΟΣΘΗΚΗ") }
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Κύριο περιεχόμενο: Λίστα Οχημάτων
            EntriesPanel(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToEntryList = onNavigateToEntryList,
                onNavigateToEditVehicle = onNavigateToVehicleForm // Χρησιμοποιεί την ίδια πλοήγηση
            )

            // TODO: Εφαρμογή swipe για Statistics (αριστερά) και Preferences (δεξιά)
            // Αυτή η λογική θα χρειαστεί ένα NavController για να διαχειριστεί τις οθόνες.
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
        title = { Text("") }, // Άδειο Title
        navigationIcon = {
            // Logo (Τέρμα πάνω αριστερά)
            IconButton(onClick = onLogoClick) {
                // Προσομοίωση Logo Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo_main), // ΥΠΟΘΕΤΩ R.drawable.ic_app_logo_main
                    contentDescription = "Λογότυπο Εφαρμογής",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        actions = {
            // Preferences (Τέρμα πάνω δεξιά)
            IconButton(onClick = onPreferencesClick) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ρυθμίσεις",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, // Κάνουμε την TopBar διαφανή
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// Χρειάζεται να επιβεβαιώσουμε ότι τα R.drawable assets υπάρχουν και στο R.kt
// Αν δεν υπάρχουν, θα έχουμε Unresolved reference.