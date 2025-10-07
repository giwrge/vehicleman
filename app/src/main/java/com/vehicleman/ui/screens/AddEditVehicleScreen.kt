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
import com.vehicleman.presentation.vehicles.AddEditVehicleViewModel
import com.vehicleman.presentation.vehicles.VehicleFormEvent

// Προκαθορισμένοι τύποι καυσίμων για το Dropdown
private val fuelTypes = listOf("Βενζίνη", "Diesel", "Υγραέριο (LPG)", "Φυσικό Αέριο (CNG)", "Ηλεκτρικό")

/**
 * Οθόνη 1: Φόρμα Προσθήκης/Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class) // ΑΠΑΡΑΙΤΗΤΟ για ExposedDropdownMenuBox και TopAppBar
@Composable
fun AddEditVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVehicleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val validationErrors = state.validationErrors
    val isReady = !state.isLoading

    // Λογική για πλοήγηση πίσω αν η αποθήκευση ήταν επιτυχής
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isEditMode) "Επεξεργασία Οχήματος" else "Προσθήκη Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Χρησιμοποιούμε το Icons.Filled.ArrowBack
                        Icon(
                            imageVector = Icons.Filled.ArrowBack, // ΑΛΛΑΓΗ: Χρησιμοποιούμε το απλό Filled.ArrowBack
                            contentDescription = "Πίσω"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                    label = { Text("Όνομα Οχήματος (π.χ. 'Ηλεκτρικό' ή 'Φορτηγό')") },
                    isError = validationErrors.nameError != null,
                    supportingText = { if (validationErrors.nameError != null) Text(validationErrors.nameError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΜΑΡΚΑ
                OutlinedTextField(
                    value = state.make,
                    onValueChange = { viewModel.onEvent(VehicleFormEvent.MakeChanged(it)) },
                    label = { Text("Μάρκα (π.χ. 'Honda')") },
                    isError = validationErrors.makeError != null,
                    supportingText = { if (validationErrors.makeError != null) Text(validationErrors.makeError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΜΟΝΤΕΛΟ
                OutlinedTextField(
                    value = state.model,
                    onValueChange = { viewModel.onEvent(VehicleFormEvent.ModelChanged(it)) },
                    label = { Text("Μοντέλο (π.χ. 'Civic Type R')") },
                    isError = validationErrors.modelError != null,
                    supportingText = { if (validationErrors.modelError != null) Text(validationErrors.modelError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΕΤΟΣ
                OutlinedTextField(
                    value = state.year,
                    onValueChange = { viewModel.onEvent(VehicleFormEvent.YearChanged(it)) },
                    label = { Text("Έτος") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = validationErrors.yearError != null,
                    supportingText = { if (validationErrors.yearError != null) Text(validationErrors.yearError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΠΙΝΑΚΙΔΑ
                OutlinedTextField(
                    value = state.licensePlate,
                    onValueChange = { viewModel.onEvent(VehicleFormEvent.LicensePlateChanged(it)) },
                    label = { Text("Πινακίδα (π.χ. ΙΟΝ-7700)") },
                    isError = validationErrors.licensePlateError != null,
                    supportingText = { if (validationErrors.licensePlateError != null) Text(validationErrors.licensePlateError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΤΥΠΟΣ ΚΑΥΣΙΜΟΥ (Dropdown Menu)
                FuelTypeDropdown(
                    selectedFuelType = state.fuelType,
                    onSelect = { viewModel.onEvent(VehicleFormEvent.FuelTypeChanged(it)) } // Χρησιμοποιεί το σωστό event
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ΑΡΧΙΚΟΣ ΧΙΛΙΟΜΕΤΡΗΤΗΣ
                OutlinedTextField(
                    value = state.initialOdometer,
                    onValueChange = { viewModel.onEvent(VehicleFormEvent.InitialOdometerChanged(it)) },
                    label = { Text("Αρχικός Χιλιομετρητής") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = validationErrors.initialOdometerError != null,
                    supportingText = { if (validationErrors.initialOdometerError != null) Text(validationErrors.initialOdometerError) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))


                // ΚΟΥΜΠΙ ΑΠΟΘΗΚΕΥΣΗΣ
                Button(
                    onClick = { viewModel.onEvent(VehicleFormEvent.SaveVehicle) },
                    enabled = isReady, // Το κουμπί ενεργοποιείται όταν η φόρμα είναι έτοιμη
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (state.isEditMode) "ΑΠΟΘΗΚΕΥΣΗ ΑΛΛΑΓΩΝ" else "ΠΡΟΣΘΗΚΗ ΟΧΗΜΑΤΟΣ")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Μήνυμα Λάθους (generalError)
                if (validationErrors.generalError != null) {
                    Text(
                        text = "Σφάλμα: ${validationErrors.generalError}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                    // TODO: Εδώ θα μπορούσε να υπάρχει και ένα κουμπί για DismissGeneralError
                }

                // Paywall (αν υπάρχει)
                if (state.showPaywall) {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Text("Η δωρεάν έκδοση υποστηρίζει μόνο ένα όχημα. Απαιτείται αναβάθμιση.", modifier = Modifier.padding(16.dp))
                        Button(onClick = { /* TODO: Navigate to Paywall screen */ }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
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
