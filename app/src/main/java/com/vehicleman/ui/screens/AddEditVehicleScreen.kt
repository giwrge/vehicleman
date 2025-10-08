package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormViewModel
import com.vehicleman.ui.theme.VehicleManTheme

// Προκαθορισμένοι τύποι καυσίμων για το Dropdown
private val fuelTypes = listOf("Βενζίνη", "Diesel", "Υγραέριο (LPG)", "Φυσικό Αέριο (CNG)", "Ηλεκτρικό")

/**
 * Οθόνη Φόρμας Προσθήκης/Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Χειρισμός Επιτυχούς Αποθήκευσης
    LaunchedEffect(state.isSavedSuccess) {
        if (state.isSavedSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isEditMode) "Επεξεργασία Οχήματος" else "Προσθήκη Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Πίσω"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // Κλειδώνουμε το state.validationErrors σε μια τοπική μεταβλητή για το Smart Cast
        val errors = state.validationErrors

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ΟΝΟΜΑ ΟΧΗΜΑΤΟΣ
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.NameChanged(it)) },
                label = { Text("Όνομα Οχήματος (π.χ. 'Ηλεκτρικό')") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.nameError != null,
                supportingText = {
                    if (errors.nameError != null) {
                        // Χρησιμοποιούμε 'errors.nameError!!' αφού το ελέγξαμε στο isError
                        Text(text = errors.nameError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΜΑΡΚΑ
            OutlinedTextField(
                value = state.make,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.MakeChanged(it)) },
                label = { Text("Μάρκα (π.χ. 'Honda')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΜΟΝΤΕΛΟ
            OutlinedTextField(
                value = state.model,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.ModelChanged(it)) },
                label = { Text("Μοντέλο (π.χ. 'Civic Type R')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΕΤΟΣ
            OutlinedTextField(
                value = state.year,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.YearChanged(it)) },
                label = { Text("Έτος") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΠΙΝΑΚΙΔΑ
            OutlinedTextField(
                value = state.licensePlate,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.LicensePlateChanged(it)) },
                label = { Text("Πινακίδα (π.χ. ΙΟΝ-7700)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΤΥΠΟΣ ΚΑΥΣΙΜΟΥ (Dropdown Menu)
            FuelTypeDropdown(
                selectedFuelType = state.fuelType,
                onSelect = { viewModel.onEvent(VehicleFormEvent.FuelTypeSelected(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΑΡΧΙΚΟΣ ΧΙΛΙΟΜΕΤΡΗΤΗΣ
            OutlinedTextField(
                value = state.initialOdometer,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.InitialOdometerChanged(it)) },
                label = { Text("Αρχικός Χιλιομετρητής") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = errors.initialOdometerError != null,
                supportingText = {
                    if (errors.initialOdometerError != null) {
                        // Χρησιμοποιούμε 'errors.initialOdometerError!!' αφού το ελέγξαμε στο isError
                        Text(text = errors.initialOdometerError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Μήνυμα Λάθους (General Error)
            if (errors.generalError != null) {
                Text(
                    text = "Σφάλμα: ${errors.generalError!!}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // ΚΟΥΜΠΙ ΑΠΟΘΗΚΕΥΣΗΣ
            Button(
                onClick = { viewModel.onEvent(VehicleFormEvent.SaveVehicle) },
                enabled = state.isReady && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (state.isEditMode) "ΑΠΟΘΗΚΕΥΣΗ ΑΛΛΑΓΩΝ" else "ΠΡΟΣΘΗΚΗ ΟΧΗΜΑΤΟΣ")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Paywall (αν υπάρχει)
            if (state.showPaywall) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Έχετε φτάσει το όριο των οχημάτων.", style = MaterialTheme.typography.titleMedium)
                        Text("Η δωρεάν έκδοση υποστηρίζει μόνο ένα όχημα. Απαιτείται αναβάθμιση.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { /* TODO: Navigate to Paywall screen */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Αναβάθμιση σε PRO")
                        }
                    }
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