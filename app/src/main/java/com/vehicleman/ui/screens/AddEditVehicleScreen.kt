package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    navController: NavController,
    vehicleId: String?,
    addEditVehicleViewModel: AddEditVehicleViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val state by addEditVehicleViewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        if (vehicleId != null && vehicleId != "new") {
            addEditVehicleViewModel.onEvent(VehicleFormEvent.LoadVehicle(vehicleId))
        } else {
            addEditVehicleViewModel.resetState()
        }
    }

    LaunchedEffect(state.isFormValid) {
        if (state.isFormValid) {
            addEditVehicleViewModel.resetState()
            navController.popBackStack()
        }
    }

    LaunchedEffect(state.shouldNavigateToProMode) {
        if (state.shouldNavigateToProMode) {
            navController.navigate(NavDestinations.PRO_MODE_ROUTE)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_home_background_night) else painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopBar(
                    state = state,
                    navController = navController,
                    onSave = { addEditVehicleViewModel.onEvent(VehicleFormEvent.Submit) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    state: VehicleFormState,
    navController: NavController,
    onSave: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = { Text(if (state.id == null) "Προσθήκη Οχήματος" else "Επεξεργασία Οχήματος", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 16.dp)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                Image(painter = painterResource(id = R.mipmap.ic_backarrow), contentDescription = "Πίσω")
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Image(painter = painterResource(id = R.mipmap.ic_save), contentDescription = "Αποθήκευση")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleForm(
    modifier: Modifier = Modifier,
    state: VehicleFormState,
    onEvent: (VehicleFormEvent) -> Unit
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(value = state.make, onValueChange = { onEvent(VehicleFormEvent.MakeChanged(it)) }, label = { Text("Μάρκα") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        OutlinedTextField(value = state.model, onValueChange = { onEvent(VehicleFormEvent.ModelChanged(it)) }, label = { Text("Μοντέλο") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        OutlinedTextField(value = state.plateNumber, onValueChange = { onEvent(VehicleFormEvent.PlateNumberChanged(it)) }, label = { Text("Αριθμός Πινακίδας") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        OutlinedTextField(value = state.year, onValueChange = { onEvent(VehicleFormEvent.YearChanged(it)) }, label = { Text("Έτος Κατασκευής") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        OutlinedTextField(
            value = state.currentOdometer,
            onValueChange = { onEvent(VehicleFormEvent.CurrentOdometerChanged(it)) },
            label = { Text("Χιλιόμετρα (Οδόμετρο)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        FuelTypeSelector(selectedFuelTypes = state.fuelTypes, onFuelTypeChanged = { onEvent(VehicleFormEvent.FuelTypeChanged(it)) })

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)

        Text("Συντήρηση", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

        OutlinedTextField(
            value = state.oilChangeKm,
            onValueChange = { onEvent(VehicleFormEvent.OilChangeKmChanged(it)) },
            label = { Text("Αλλαγή Λαδιών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )
        DatePickerField(label = "Αλλαγή Λαδιών (ημερομηνία)", value = state.oilChangeDate, onValueChange = { onEvent(VehicleFormEvent.OilChangeDateChanged(it)) }, colors = textFieldColors)
        OutlinedTextField(
            value = state.tiresChangeKm,
            onValueChange = { onEvent(VehicleFormEvent.TiresChangeKmChanged(it)) },
            label = { Text("Αλλαγή Ελαστικών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )
        DatePickerField(label = "Αλλαγή Ελαστικών (ημερομηνία)", value = state.tiresChangeDate, onValueChange = { onEvent(VehicleFormEvent.TiresChangeDateChanged(it)) }, colors = textFieldColors)

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)

        Text("Ασφάλεια & Τέλη", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

        DatePickerField(label = "Ημ/νία Λήξης Ασφάλειας", value = state.insuranceExpiryDate, onValueChange = { onEvent(VehicleFormEvent.InsuranceExpiryDateChanged(it)) }, colors = textFieldColors)
        DatePickerField(label = "Ημ/νία Λήξης Τελών", value = state.taxesExpiryDate, onValueChange = { onEvent(VehicleFormEvent.TaxesExpiryDateChanged(it)) }, colors = textFieldColors)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeSelector(selectedFuelTypes: String, onFuelTypeChanged: (String) -> Unit) {
    val fuelOptions = listOf("Gasoline", "Diesel", "LPG", "CNG", "Electric")
    var showDialog by remember { mutableStateOf(false) }
    
    val disabledTextFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Box(modifier = Modifier.clickable { showDialog = true }) {
        OutlinedTextField(
            value = selectedFuelTypes,
            onValueChange = {},
            readOnly = true,
            label = { Text("Τύπος Καυσίμου") },
            modifier = Modifier.fillMaxWidth(),
            colors = disabledTextFieldColors,
            enabled = false
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Επιλογή Καυσίμου") },
            text = {
                Column {
                    fuelOptions.forEach { fuelType ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedFuelTypes.contains(fuelType),
                                onCheckedChange = {
                                    val currentTypes = selectedFuelTypes.split(", ").filter { it.isNotBlank() }.toMutableSet()
                                    if (it) {
                                        currentTypes.add(fuelType)
                                    } else {
                                        currentTypes.remove(fuelType)
                                    }
                                    onFuelTypeChanged(currentTypes.joinToString(", "))
                                }
                            )
                            Text(fuelType, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = Locale("el")
    val dateFormatter = SimpleDateFormat("EEEE, dd/MM/yyyy", locale)
    
    val disabledTextFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Box(modifier = Modifier.clickable { showDatePicker = true }) {
        OutlinedTextField(
            value = value.toLongOrNull()?.let { dateFormatter.format(Date(it)) } ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            colors = disabledTextFieldColors,
            enabled = false // Make the text field non-editable but clickable via the Box
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value.toLongOrNull() ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onValueChange(it.toString())
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
