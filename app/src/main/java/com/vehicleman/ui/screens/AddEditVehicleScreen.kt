package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel

/**
 * Οθόνη Προσθήκης / Επεξεργασίας Οχήματος.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    navController: NavController,
    addEditVehicleViewModel: AddEditVehicleViewModel
) {
    val state by addEditVehicleViewModel.state.collectAsState()

    // Αυτόματη επιστροφή στην προηγούμενη οθόνη μετά από επιτυχή αποθήκευση
    LaunchedEffect(state.isFormValid) {
        if (state.isFormValid) {
            addEditVehicleViewModel.resetState() // Reset the form state
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (state.id == null) "Προσθήκη Οχήματος" else "Επεξεργασία Οχήματος") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Πίσω"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { addEditVehicleViewModel.onEvent(VehicleFormEvent.Submit) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Αποθήκευση"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            AddEditVehicleForm(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                state = state,
                onEvent = addEditVehicleViewModel::onEvent
            )

            state.errorMessage?.let {
                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(it) {
                    snackbarHostState.showSnackbar(it)
                }
                SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(innerPadding))
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
    state: VehicleFormState,
    onEvent: (VehicleFormEvent) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = state.make,
            onValueChange = { onEvent(VehicleFormEvent.MakeChanged(it)) },
            label = { Text("Μάρκα") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.model,
            onValueChange = { onEvent(VehicleFormEvent.ModelChanged(it)) },
            label = { Text("Μοντέλο") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.plateNumber,
            onValueChange = { onEvent(VehicleFormEvent.PlateNumberChanged(it)) },
            label = { Text("Αριθμός Πινακίδας") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.year,
            onValueChange = { onEvent(VehicleFormEvent.YearChanged(it)) },
            label = { Text("Έτος Κατασκευής") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.currentOdometer,
            onValueChange = { onEvent(VehicleFormEvent.CurrentOdometerChanged(it)) },
            label = { Text("Χιλιόμετρα (Οδόμετρο)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.fuelType,
            onValueChange = { onEvent(VehicleFormEvent.FuelTypeChanged(it)) },
            label = { Text("Τύπος Καυσίμου") },
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Συντήρηση", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.oilChangeKm?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.OilChangeKmChanged(it)) },
            label = { Text("Αλλαγή Λαδιών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.oilChangeDate?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.OilChangeDateChanged(it)) },
            label = { Text("Αλλαγή Λαδιών (ημερομηνία)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.tiresChangeKm?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.TiresChangeKmChanged(it)) },
            label = { Text("Αλλαγή Ελαστικών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.tiresChangeDate?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.TiresChangeDateChanged(it)) },
            label = { Text("Αλλαγή Ελαστικών (ημερομηνία)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Ασφάλεια & Τέλη", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.insuranceExpiryDate?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.InsuranceExpiryDateChanged(it)) },
            label = { Text("Ημ/νία Λήξης Ασφάλειας") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.taxesExpiryDate?.toString() ?: "",
            onValueChange = { onEvent(VehicleFormEvent.TaxesExpiryDateChanged(it)) },
            label = { Text("Ημ/νία Λήξης Τελών") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}