package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.ui.viewmodels.AddEditVehicleEvent
import com.vehicleman.ui.viewmodels.AddEditVehicleViewModel // <--- Χρησιμοποιεί το AddEditViewModel

// Προκαθορισμένοι τύποι καυσίμων για το Dropdown
private val fuelTypes = listOf("Βενζίνη", "Diesel", "Υγραέριο (LPG)", "Φυσικό Αέριο (CNG)", "Ηλεκτρικό")

/**
 * Οθόνη 1: Φόρμα Προσθήκης/Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class) // ΑΠΑΡΑΙΤΗΤΟ για ExposedDropdownMenuBox και TopAppBar
@Composable
fun AddEditVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVehicleViewModel = hiltViewModel() // Χρήση του AddEdit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Λογική πλοήγησης μετά την επιτυχή αποθήκευση
    LaunchedEffect(state.isSavedSuccessfully) {
        if (state.isSavedSuccessfully) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (state.isEditMode) "Επεξεργασία Οχήματος" else "Προσθήκη Νέου Οχήματος")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ----------------------------------------------------
            // 1. Φόρμα Εισόδου
            // ----------------------------------------------------

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.NameChanged(it)) },
                label = { Text("Όνομα Οχήματος (Υποχρεωτικό)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                isError = state.error?.contains("Όνομα") == true && state.name.isBlank()
            )

            // Μάρκα και Μοντέλο
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.make,
                    onValueChange = { viewModel.onEvent(AddEditVehicleEvent.MakeChanged(it)) },
                    label = { Text("Μάρκα") },
                    modifier = Modifier.weight(1f).padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.model,
                    onValueChange = { viewModel.onEvent(AddEditVehicleEvent.ModelChanged(it)) },
                    label = { Text("Μοντέλο") },
                    modifier = Modifier.weight(1f).padding(bottom = 8.dp)
                )
            }

            // Έτος και Πινακίδα
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.year,
                    onValueChange = { viewModel.onEvent(AddEditVehicleEvent.YearChanged(it)) },
                    label = { Text("Έτος") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.licensePlate,
                    onValueChange = { viewModel.onEvent(AddEditVehicleEvent.LicensePlateChanged(it)) },
                    label = { Text("Πινακίδα") },
                    modifier = Modifier.weight(1f).padding(bottom = 8.dp)
                )
            }

            // Τύπος Καυσίμου (Dropdown Menu)
            FuelTypeDropdown(
                selectedFuelType = state.fuelType,
                onSelect = { viewModel.onEvent(AddEditVehicleEvent.FuelTypeSelected(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Αρχικά Χιλιόμετρα (Υποχρεωτικό)
            OutlinedTextField(
                value = state.initialOdometer,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.InitialOdometerChanged(it)) },
                label = { Text("Αρχικά Χιλιόμετρα (Υποχρεωτικό)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                suffix = { Text("km") },
                isError = state.error?.contains("Χιλιόμετρα") == true
            )

            // ----------------------------------------------------
            // 2. Λογική Μονετοποίησης & Μηνύματα
            // ----------------------------------------------------

            // Εμφάνιση μηνύματος σφάλματος ή Paywall
            if (state.showPaywall || state.error != null) {
                val isPaywall = state.showPaywall
                val containerColor = if (isPaywall) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                val textColor = if (isPaywall) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant

                Card(
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(
                        text = state.error ?: "Γενικό Σφάλμα Φόρμας.",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ----------------------------------------------------
            // 3. Κουμπί Αποθήκευσης
            // ----------------------------------------------------

            Button(
                onClick = { viewModel.onEvent(AddEditVehicleEvent.SaveVehicle) },
                enabled = !state.showPaywall,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(text = if (state.isEditMode) "Ενημέρωση Οχήματος" else "Αποθήκευση Οχήματος")
            }

            if (state.showPaywall) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Πλοήγηση στο Paywall */ }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Αναβάθμιση σε PRO")
                }
            }
        }
    }
}

/** Βοηθητικό Composable για το Dropdown Menu του Τύπου Καυσίμου. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelTypeDropdown(
    selectedFuelType: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedFuelType,
            onValueChange = { },
            label = { Text("Τύπος Καυσίμου") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fuelTypes.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelect(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
