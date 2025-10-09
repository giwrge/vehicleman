package com.vehicleman.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    vehicleId: String,
    onNavigateBack: () -> Unit,
    viewModel: VehicleFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Εμφάνιση Snackbar για σφάλματα
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = "Σφάλμα: $it",
                actionLabel = "OK"
            )
        }
    }

    // Πλοήγηση μετά την επιτυχή αποθήκευση
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.onEvent(VehicleFormEvent.NavigationDone)
            onNavigateBack()
        }
    }

    // State για τον χειρισμό του DatePicker
    var showDatePicker by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (vehicleId.isBlank() || vehicleId == "new") "Προσθήκη Νέου Οχήματος" else "Επεξεργασία Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { viewModel.onEvent(VehicleFormEvent.OnSaveVehicleClick) },
                    enabled = !state.isLoading
                ) {
                    Text(if (state.isLoading) "Αποθήκευση..." else "Αποθήκευση Οχήματος")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && !state.isSaved && state.error == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- ΒΑΣΙΚΑ ΣΤΟΙΧΕΙΑ ΟΧΗΜΑΤΟΣ ---
                    Text(
                        "Βασικά Στοιχεία",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnNameChange(it)) },
                        label = { Text("Όνομα (Ψευδώνυμο)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.nameError != null,
                        supportingText = { state.nameError?.let { Text(it) } }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = state.make,
                            onValueChange = { viewModel.onEvent(VehicleFormEvent.OnMakeChange(it)) },
                            label = { Text("Κατασκευαστής") },
                            modifier = Modifier.weight(1f),
                            isError = state.makeError != null,
                            supportingText = { state.makeError?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = state.model,
                            onValueChange = { viewModel.onEvent(VehicleFormEvent.OnModelChange(it)) },
                            label = { Text("Μοντέλο") },
                            modifier = Modifier.weight(1f),
                            isError = state.modelError != null,
                            supportingText = { state.modelError?.let { Text(it) } }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.year,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnYearChange(it)) },
                        label = { Text("Έτος Κατασκευής") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.yearError != null,
                        supportingText = { state.yearError?.let { Text(it) } }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.licensePlate,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnLicensePlateChange(it)) },
                        label = { Text("Πινακίδα") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.fuelType,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnFuelTypeChange(it)) },
                        label = { Text("Τύπος Καυσίμου") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.initialOdometer,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnInitialOdometerChange(it)) },
                        label = { Text("Αρχική Ένδειξη Χιλιομετρητή (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.initialOdometerError != null,
                        supportingText = { state.initialOdometerError?.let { Text(it) } }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Picker Input
                    OutlinedTextField(
                        value = state.registrationDate.toLocalizedDateString(),
                        onValueChange = { /* Read-only */ },
                        label = { Text("Ημερομηνία Πρώτης Άδειας") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Επιλογή Ημερομηνίας")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // --- ΡΥΘΜΙΣΕΙΣ ΣΥΝΤΗΡΗΣΗΣ (AIRFLOW) ---
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "Προσαρμοσμένα Διαστήματα Συντήρησης (Airflow)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.oilChangeIntervalKm,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnOilChangeKmChange(it)) },
                        label = { Text("Αλλαγή Λαδιών: Διάστημα σε km") },
                        placeholder = { Text("Π.χ. 15000 (Προεπιλογή: 10000)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.oilChangeIntervalDays,
                        onValueChange = { viewModel.onEvent(VehicleFormEvent.OnOilChangeDaysChange(it)) },
                        label = { Text("Αλλαγή Λαδιών: Διάστημα σε Ημέρες") },
                        placeholder = { Text("Π.χ. 365 (Προεπιλογή: 365)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Date Picker Dialog Logic (Χρειάζεται να υλοποιηθεί το Material3 Date Picker)
    if (showDatePicker) {
        // Placeholder για το DatePicker. Στην κανονική εφαρμογή θα χρησιμοποιούσατε
        // το DatePickerDialog ή DateRangePicker του Material3.
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Επιλογή Ημερομηνίας Άδειας") },
            text = { Text("Πατήστε OK για να ορίσετε τη σημερινή ημερομηνία (Placeholder)") },
            confirmButton = {
                Button(onClick = {
                    viewModel.onEvent(VehicleFormEvent.OnRegistrationDateChange(Date()))
                    showDatePicker = false
                }) {
                    Text("ΟΚ")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false }) {
                    Text("Ακύρωση")
                }
            }
        )
    }
}

// Helper extension function (πρέπει να υπάρχει στο project σας, π.χ. σε util)
fun Date.toLocalizedDateString(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}
