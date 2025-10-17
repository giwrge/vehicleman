package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save

/**
 * Οθόνη Προσθήκης / Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    navController: NavController,
    viewModel: AddEditVehicleViewModel = hiltViewModel(),
    vehicleId: String? = null
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        vehicleId?.let {
            viewModel.onEvent(VehicleFormEvent.LoadVehicle(it))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vehicleId == null) "Προσθήκη Οχήματος" else "Επεξεργασία Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(VehicleFormEvent.SaveVehicle) },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Αποθήκευση")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AddEditVehicleForm(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                state = state,
                onValueChange = { field, value ->
                    viewModel.onEvent(VehicleFormEvent.FieldChanged(field, value))
                }
            )
        }

        // Εμφάνιση σφάλματος αν υπάρχει
        state.errorMessage?.let { error ->
            SnackbarHost(hostState = remember { SnackbarHostState() }) {
                Snackbar { Text(text = error) }
            }
        }

        // Αν έγινε επιτυχής αποθήκευση
        if (state.success) {
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
    }
}

/**
 * Κύρια φόρμα για τα πεδία του οχήματος.
 */
@Composable
fun AddEditVehicleForm(
    modifier: Modifier = Modifier,
    state: com.vehicleman.presentation.vehicles.VehicleFormState,
    onValueChange: (String, String) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.brand,
            onValueChange = { onValueChange("brand", it) },
            label = { Text("Μάρκα") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.model,
            onValueChange = { onValueChange("model", it) },
            label = { Text("Μοντέλο") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.plate,
            onValueChange = { onValueChange("plate", it) },
            label = { Text("Αριθμός Πινακίδας") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.year,
            onValueChange = { onValueChange("year", it) },
            label = { Text("Έτος Κατασκευής") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.odometer,
            onValueChange = { onValueChange("odometer", it) },
            label = { Text("Χιλιόμετρα (Οδόμετρο)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Συντήρηση", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.oilChangeKm,
            onValueChange = { onValueChange("oilChangeKm", it) },
            label = { Text("Αλλαγή Λαδιών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.oilChangeTime,
            onValueChange = { onValueChange("oilChangeTime", it) },
            label = { Text("Αλλαγή Λαδιών (χρονικό διάστημα)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.tiresChangeKm,
            onValueChange = { onValueChange("tiresChangeKm", it) },
            label = { Text("Αλλαγή Ελαστικών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.tiresChangeTime,
            onValueChange = { onValueChange("tiresChangeTime", it) },
            label = { Text("Αλλαγή Ελαστικών (χρονικό διάστημα)") },
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Ασφάλεια & Τέλη", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.insuranceDate,
            onValueChange = { onValueChange("insuranceDate", it) },
            label = { Text("Ημ/νία Ασφάλειας") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.taxDate,
            onValueChange = { onValueChange("taxDate", it) },
            label = { Text("Ημ/νία Τελών") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
