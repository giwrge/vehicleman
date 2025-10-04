package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
// ΔΙΟΡΘΩΣΗ: Χρειαζόμαστε τη συγκεκριμένη εισαγωγή για να αναγνωριστεί το 'automirrored'
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// Επίσης, χρειαζόμαστε την εισαγωγή για το AutoMirrored αν χρησιμοποιείται ως annotation
import androidx.compose.ui.graphics.vector.AutoMirrored
import com.vehicleman.ui.viewmodel.AddEditVehicleEvent
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel // <--- Χρησιμοποιεί το AddEditViewModel

// Προκαθορισμένοι τύποι καυσίμων για το Dropdown
private val fuelTypes = listOf("Βενζίνη", "Diesel", "Υγραέριο (LPG)", "Φυσικό Αέριο (CNG)", "Ηλεκτρικό")

/**
 * Οθόνη 1: Φόρμα Προσθήκης/Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class) // ΑΠΑΡΑΙΤΗΤΟ για ExposedDropdownMenuBox και TopAppBar
@Composable
fun AddEditVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVehicleViewModel = hiltViewModel() // Χρήση του...
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isEditMode) "Επεξεργασία Οχήματος" else "Προσθήκη Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Χρησιμοποιούμε το AutoMirrored.Filled.ArrowBack για συμβατότητα με RTL (από δεξιά προς αριστερά) γλώσσες
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Πίσω"
                        )
                    }
                }
            )
        }
    ) { padding ->
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
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.NameChanged(it)) },
                label = { Text("Όνομα Οχήματος (π.χ. 'Ηλεκτρικό' ή 'Φορτηγό')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΜΑΡΚΑ
            OutlinedTextField(
                value = state.make,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.MakeChanged(it)) },
                label = { Text("Μάρκα (π.χ. 'Honda')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΜΟΝΤΕΛΟ
            OutlinedTextField(
                value = state.model,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.ModelChanged(it)) },
                label = { Text("Μοντέλο (π.χ. 'Civic Type R')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΕΤΟΣ
            OutlinedTextField(
                value = state.year,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.YearChanged(it)) },
                label = { Text("Έτος") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΠΙΝΑΚΙΔΑ
            OutlinedTextField(
                value = state.licensePlate,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.LicensePlateChanged(it)) },
                label = { Text("Πινακίδα (π.χ. ΙΟΝ-7700)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΤΥΠΟΣ ΚΑΥΣΙΜΟΥ (Dropdown Menu)
            FuelTypeDropdown(
                selectedFuelType = state.fuelType,
                onSelect = { viewModel.onEvent(AddEditVehicleEvent.FuelTypeChanged(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ΑΡΧΙΚΟΣ ΧΙΛΙΟΜΕΤΡΗΤΗΣ
            OutlinedTextField(
                value = state.initialOdometer,
                onValueChange = { viewModel.onEvent(AddEditVehicleEvent.InitialOdometerChanged(it)) },
                label = { Text("Αρχικός Χιλιομετρητής") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))


            // ΚΟΥΜΠΙ ΑΠΟΘΗΚΕΥΣΗΣ
            Button(
                onClick = { viewModel.onEvent(AddEditVehicleEvent.SaveVehicle) },
                enabled = state.isReady, // Το κουμπί ενεργοποιείται όταν η φόρμα είναι έτοιμη
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (state.isEditMode) "ΑΠΟΘΗΚΕΥΣΗ ΑΛΛΑΓΩΝ" else "ΠΡΟΣΘΗΚΗ ΟΧΗΜΑΤΟΣ")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Μήνυμα Λάθους (αν υπάρχει)
            if (state.error != null) {
                Text(
                    text = "Σφάλμα: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
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
