package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R
import com.vehicleman.presentation.entries.EntriesPanelEvent // ΣΩΣΤΟ: Το Event πλέον ανήκει εδώ
import com.vehicleman.presentation.entries.EntriesPanelViewModel // ΣΩΣΤΟ: Το ViewModel πλέον ανήκει εδώ
import com.vehicleman.ui.panels.EntriesPanel

/**
 * Η κύρια οθόνη της εφαρμογής.
 * Εμφανίζει τη λίστα οχημάτων και το FAB για την προσθήκη νέου οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Συνάρτηση πλοήγησης για άνοιγμα της φόρμας προσθήκης/επεξεργασίας.
    onNavigateToEntryForm: (String) -> Unit,
    // ViewModel για τη διαχείριση της λίστας οχημάτων.
    viewModel: EntriesPanelViewModel = hiltViewModel()
) {
    // Παρατηρούμε την κατάσταση (State) από τον ViewModel
    val state by viewModel.state.collectAsState()

    // Ελέγχουμε αν είναι ενεργό το Selection Mode
    val isSelectionMode = state.selectedVehicleIds.isNotEmpty()

    // Ελέγχουμε αν υπάρχει ακριβώς ένα επιλεγμένο όχημα για επεξεργασία
    val isSingleSelection = state.selectedVehicleIds.size == 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
                // Μπορούμε να προσθέσουμε εδώ actions (settings, search) αν χρειαστεί
            )
        },
        floatingActionButton = {
            // Το FAB εμφανίζεται μόνο όταν ΔΕΝ είμαστε σε Selection Mode
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToEntryForm("new") }, // Πλοήγηση για Προσθήκη νέου (ID="new")
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Προσθήκη Οχήματος") },
                    text = { Text("Νέο Όχημα") },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Το EntriesPanel εμφανίζει τη λίστα
            EntriesPanel(
                vehicles = state.vehicles,
                isSelectionMode = isSelectionMode,
                selectedVehicleIds = state.selectedVehicleIds,
                // EVENTS (Στέλνονται πίσω στον ViewModel)
                onEvent = { event ->
                    when (event) {
                        is EntriesPanelEvent.VehicleClicked -> {
                            // Αν δεν είναι σε Selection Mode, πλοήγηση για να δει τις καταχωρήσεις του οχήματος.
                            if (!isSelectionMode) {
                                // TODO: Εδώ θα πάμε στην οθόνη VehicleDetail/EntriesList
                                println("Go to Entries List for Vehicle ID: ${event.vehicleId}")
                            } else {
                                // Αν είναι σε Selection Mode, κάνουμε toggle την επιλογή
                                viewModel.onEvent(EntriesPanelEvent.VehicleToggled(event.vehicleId))
                            }
                        }
                        is EntriesPanelEvent.VehicleLongClicked -> {
                            // Πάντα στέλνουμε το Long Click στον ViewModel για να μπει σε Selection Mode
                            viewModel.onEvent(EntriesPanelEvent.VehicleLongClicked(event.vehicleId))
                        }
                        EntriesPanelEvent.EditSelectedVehicle -> {
                            // Αν είναι σε Selection Mode και έχει επιλεγεί 1, πάμε για επεξεργασία
                            if (isSingleSelection) {
                                val selectedId = state.selectedVehicleIds.first()
                                onNavigateToEntryForm(selectedId) // Πλοήγηση για Επεξεργασία (με το πραγματικό ID)
                                viewModel.onEvent(EntriesPanelEvent.ExitSelectionMode) // Βγαίνουμε από το Selection Mode
                            }
                        }
                        else -> viewModel.onEvent(event) // Όλα τα άλλα events (Delete, ExitSelection)
                    }
                }
            )

            // Εμφάνιση loading, error, ή empty state αν χρειαστεί
            if (state.isLoading) {
                // TODO: Καλύτερο loading indicator
                Text("Φόρτωση οχημάτων...", modifier = Modifier.align(Alignment.Center))
            }
            if (state.error != null) {
                // TODO: Καλύτερο error message
                Text("Σφάλμα: ${state.error}", modifier = Modifier.align(Alignment.Center))
            }
            if (!state.isLoading && state.vehicles.isEmpty()) {
                Text("Δεν υπάρχουν οχήματα. Προσθέστε ένα!", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
