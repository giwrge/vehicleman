package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.R
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormViewModel

/**
 * AddEditVehicleScreen
 * Εισαγωγή ή επεξεργασία στοιχείων οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    vehicleId: String = "new",
    isFreeVersion: Boolean = true, // έλεγχος για free vs pro mode
    currentVehicleCount: Int = 0,  // αριθμός οχημάτων στη βάση
    onNavigateBack: () -> Unit,
    viewModel: VehicleFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Αν κάνουμε Edit, να φορτωθεί το όχημα
    LaunchedEffect(vehicleId) {
        if (vehicleId != "new") {
            viewModel.onEvent(VehicleFormEvent.LoadVehicle(vehicleId))
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vehicleId == "new") "Νέο Όχημα" else "Επεξεργασία Οχήματος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_app_logo_main),
                            contentDescription = "Λογότυπο",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Έλεγχος free version
                    if (isFreeVersion && vehicleId == "new" && currentVehicleCount >= 3) {
                        viewModel.showLimitReachedMessage()
                    } else {
                        viewModel.onEvent(VehicleFormEvent.SaveVehicle(state.toVehicle(vehicleId)))
                        onNavigateBack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Αποθήκευση")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VehicleTextField(
                label = "Μάρκα",
                value = state.brand,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("brand", it)) }
            )
            VehicleTextField(
                label = "Μοντέλο",
                value = state.model,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("model", it)) }
            )
            VehicleTextField(
                label = "Πινακίδα",
                value = state.plate,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("plate", it)) }
            )
            VehicleTextField(
                label = "Έτος Κατασκευής",
                value = state.year,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("year", it)) }
            )
            VehicleTextField(
                label = "Διανυθέντα Χιλιόμετρα",
                value = state.odometer,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("odometer", it)) }
            )

            Divider()

            Text("Airflow Settings", style = MaterialTheme.typography.titleMedium)

            VehicleTextField(
                label = "Αλλαγή Λαδιών (Χρόνος)",
                value = state.oilChangeTime,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("oilChangeTime", it)) }
            )
            VehicleTextField(
                label = "Αλλαγή Λαδιών (Χλμ)",
                value = state.oilChangeKm,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("oilChangeKm", it)) }
            )
            VehicleTextField(
                label = "Αλλαγή Ελαστικών (Χρόνος)",
                value = state.tiresChangeTime,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("tiresChangeTime", it)) }
            )
            VehicleTextField(
                label = "Αλλαγή Ελαστικών (Χλμ)",
                value = state.tiresChangeKm,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("tiresChangeKm", it)) }
            )
            VehicleTextField(
                label = "Πληρωμή Ασφαλίστρων (Ημερομηνία)",
                value = state.insuranceDate,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("insuranceDate", it)) }
            )
            VehicleTextField(
                label = "Πληρωμή Φόρων / Τελών Κυκλοφορίας",
                value = state.taxDate,
                onValueChange = { viewModel.onEvent(VehicleFormEvent.FieldChanged("taxDate", it)) }
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (state.limitReached) {
                Text(
                    text = "Η δωρεάν έκδοση επιτρέπει έως 3 οχήματα.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/**
 * Επαναχρησιμοποιήσιμο TextField για φόρμα οχήματος
 */
@Composable
private fun VehicleTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Μετατροπή state -> Vehicle
 */
private fun com.vehicleman.presentation.vehicles.VehicleFormState.toVehicle(vehicleId: String): Vehicle {
    return Vehicle(
        id = if (vehicleId == "new") null else vehicleId,
        brand = brand,
        model = model,
        licensePlate = plate,
        year = year.toIntOrNull(),
        odometer = odometer.toIntOrNull(),
        oilChangeTime = oilChangeTime,
        oilChangeKm = oilChangeKm.toIntOrNull(),
        tiresChangeTime = tiresChangeTime,
        tiresChangeKm = tiresChangeKm.toIntOrNull(),
        insurancePaymentDate = insuranceDate,
        taxesPaymentDate = taxDate
    )
}

/**
 * Προσθήκη βοηθητικής συνάρτησης για Free version alert
 */
private fun VehicleFormViewModel.showLimitReachedMessage() {
    _state.update {
        it.copy(limitReached = true, errorMessage = null)
    }
}
