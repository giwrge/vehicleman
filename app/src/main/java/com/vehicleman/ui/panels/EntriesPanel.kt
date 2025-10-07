package com.vehicleman.presentation.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vehicleman.R // ΑΠΑΡΑΙΤΗΤΗ ΕΙΣΑΓΩΓΗ ΓΙΑ ΤΑ CUSTOM ICONS
import com.vehicleman.ui.theme.VehicleManTheme
import com.vehicleman.domain.model.Vehicle // ΕΙΣΑΓΩΓΗ ΤΟΥ ΠΡΑΓΜΑΤΙΚΟΥ DOMAIN MODEL
import androidx.compose.foundation.ExperimentalFoundationApi // ΝΕΑ ΕΙΣΑΓΩΓΗ
import androidx.compose.foundation.combinedClickable // Χρησιμοποιείται για onLongClick
// Προσθέτουμε τα απαραίτητα imports για τα Icons.Default
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.layout.PaddingValues
/**
 * Κύριο Composable που εμφανίζει τη λίστα οχημάτων και το banner δράσεων.
 */
@Composable
fun EntriesPanel(
    vehicles: List<Vehicle>,
    onSelectVehicle: (String) -> Unit,
    onNavigateToEntryForm: (String) -> Unit,
    onDeleteSelected: (Set<String>) -> Unit
) {
    // Διαχείριση της κατάστασης επιλογής (Selection Mode)
    var selectedVehicleIds by remember { mutableStateOf(emptySet<String>()) }
    val isSelectionMode = selectedVehicleIds.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Banner και Δράσεις
        AppBannerAndActions(
            isSelectionMode = isSelectionMode,
            selectedVehicleIds = selectedVehicleIds,
            onExitSelection = { selectedVehicleIds = emptySet() },
            onDeleteSelected = { onDeleteSelected(selectedVehicleIds) },
            onEditSelected = { /* Logic for editing the single selected vehicle */ }
        )

        // 2. Λίστα Οχημάτων (VehicleList/LazyColumn)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Το 'it' αναφέρεται στο κάθε στοιχείο (vehicle) μέσα στη λίστα
            items(vehicles, key = { it.id }) { vehicle ->
                VehicleCard(
                    vehicle = vehicle,
                    isSelected = selectedVehicleIds.contains(vehicle.id),
                    onClick = {
                        if (isSelectionMode) {
                            selectedVehicleIds = if (selectedVehicleIds.contains(vehicle.id)) {
                                selectedVehicleIds - vehicle.id
                            } else {
                                selectedVehicleIds + vehicle.id
                            }
                        } else {
                            onSelectVehicle(vehicle.id)
                        }
                    },
                    onLongClick = {
                        selectedVehicleIds = selectedVehicleIds + vehicle.id
                    }
                )
            }
        }
    }
}

/**
 * Placeholder Composable για την εμφάνιση της κάρτας ενός οχήματος (VehicleCard).
 * Χρησιμοποιεί @OptIn(ExperimentalFoundationApi::class) για να επιλύσει την προειδοποίηση combinedClickable.
 */
@OptIn(ExperimentalFoundationApi::class) // ΕΔΩ ΠΡΟΣΘΕΘΗΚΕ
@Composable
fun VehicleCard(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable( // Χρησιμοποιούμε το combinedClickable
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Χρησιμοποιούμε τα νέα πεδία: name, licensePlate, make/model/year
                Text(vehicle.name, style = MaterialTheme.typography.titleLarge)
                Text("Πινακίδα: ${vehicle.licensePlate} | ${vehicle.make} (${vehicle.year})", style = MaterialTheme.typography.bodyMedium)
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Επιλεγμένο",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ... (Το AppBannerAndActions παραμένει όπως το παρείχες)
@Composable
fun AppBannerAndActions(
    isSelectionMode: Boolean,
    selectedVehicleIds: Set<String>,
    onExitSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onEditSelected: () -> Unit
) {
    val isEditEnabled = selectedVehicleIds.size == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Logo / Title (ΧΡΗΣΗ CUSTOM ICON ic_app_logo_main)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.mipmap.ic_app_logo_main), // Αλλαγή σε R.mipmap
                contentDescription = "Λογότυπο Εφαρμογής / Ενότητα Καταχωρήσεων",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp),
                color = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        // 2. Action Buttons (Selection Mode)
        AnimatedVisibility(visible = isSelectionMode) {
            Row {
                // Modify Button (Icons.Default.Build) - Διατηρούμε το Material Icon
                IconButton(
                    onClick = onEditSelected,
                    enabled = isEditEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Τροποποίηση",
                        tint = if (isEditEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Delete Button (Icons.Default.Delete) - Διατηρούμε το Material Icon
                IconButton(onClick = onDeleteSelected) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Διαγραφή",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Exit Button (ΧΡΗΣΗ CUSTOM ICON ic_app_logo_main)
                IconButton(onClick = onExitSelection) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_app_logo_main), // Το custom exit icon σου (που είναι το logo)
                        contentDescription = "Έξοδος από Επιλογή",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    data class EntriesPanelState(
        val vehicles: List<Vehicle> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
}


