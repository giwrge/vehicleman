package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close // Χρειάζεται για TopAppBar του Selection Mode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
// ΕΙΣΑΓΩΓΗ ΛΟΓΙΚΗΣ
import com.vehicleman.presentation.entries.EntriesPanelEvent
import com.vehicleman.presentation.entries.EntriesPanelViewModel
import com.vehicleman.ui.panels.EntriesPanel

/**
 * Η αρχική οθόνη που εμφανίζει τη λίστα των οχημάτων (Entries Panel).
 *
 * @param onNavigateToVehicleForm Callback για πλοήγηση στη φόρμα προσθήκης/επεξεργασίας.
 * - Χρησιμοποιείται για Προσθήκη ("new") ή Επεξεργασία (vehicleId).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Τώρα δέχεται ΜΟΝΟ αυτή την παράμετρο πλοήγησης
    onNavigateToVehicleForm: (vehicleId: String) -> Unit,
    viewModel: EntriesPanelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            // Η TopAppBar για το Selection Mode εμφανίζεται μέσα στο EntriesPanel
            if (!state.isSelectionMode) {
                TopAppBar(
                    title = { Text("Διαχείριση Οχημάτων") }
                )
            }
        },
        floatingActionButton = {
            if (!state.isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Πλοήγηση για Προσθήκη Νέου Οχήματος
                        onNavigateToVehicleForm("new")
                        // Επίσης στέλνουμε το event στο ViewModel αν χρειαζόταν extra λογική
                        viewModel.onEvent(EntriesPanelEvent.AddNewVehicleClicked)
                    },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Προσθήκη Οχήματος") },
                    text = { Text("ΠΡΟΣΘΗΚΗ") }
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            EntriesPanel(
                state = state,
                onEvent = viewModel::onEvent,
                onVehicleClicked = { vehicleId ->
                    // Πλοήγηση για Επεξεργασία Υπάρχοντος Οχήματος
                    onNavigateToVehicleForm(vehicleId)
                }
            )
        }
    }
}