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
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    navController: NavController,
    vehicleId: String?,
    addEditVehicleViewModel: AddEditVehicleViewModel = hiltViewModel()
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { Text(if (state.id == null) "Προσθήκη Οχήματος" else "Επεξεργασία Οχήματος", color = Color.Black, modifier = Modifier.padding(start = 16.dp)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                            Image(painter = painterResource(id = R.mipmap.ic_backarrow), contentDescription = "Πίσω")
                        }
                    },
                    actions = {
                        IconButton(onClick = { addEditVehicleViewModel.onEvent(VehicleFormEvent.Submit) }) {
                            Image(painter = painterResource(id = R.mipmap.ic_save), contentDescription = "Αποθήκευση")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleForm(
    modifier: Modifier = Modifier,
    state: VehicleFormState,
    onEvent: (VehicleFormEvent) -> Unit
) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = Color.White.copy(alpha = 0.6f),
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black
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
            value = state.currentOdometer.let { if (it.isNotEmpty()) NumberFormat.getNumberInstance(Locale.GERMANY).format(it.toLong()) else "" },
            onValueChange = { onEvent(VehicleFormEvent.CurrentOdometerChanged(it)) },
            label = { Text("Χιλιόμετρα (Οδόμετρο)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        FuelTypeSelector(selectedFuelTypes = state.fuelType, onFuelTypeChanged = { onEvent(VehicleFormEvent.FuelTypeChanged(it)) })

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)

        Text("Συντήρηση", style = MaterialTheme.typography.titleMedium, color = Color.Black)

        OutlinedTextField(
            value = state.oilChangeKm?.let { NumberFormat.getNumberInstance(Locale.GERMANY).format(it) } ?: "",
            onValueChange = { onEvent(VehicleFormEvent.OilChangeKmChanged(it.toString())) },
            label = { Text("Αλλαγή Λαδιών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )
        DatePickerField(label = "Αλλαγή Λαδιών (ημερομηνία)", value = state.oilChangeDate, onValueChange = { onEvent(VehicleFormEvent.OilChangeDateChanged(it)) }, colors = textFieldColors)
        OutlinedTextField(
            value = state.tiresChangeKm?.let { NumberFormat.getNumberInstance(Locale.GERMANY).format(it) } ?: "",
            onValueChange = { onEvent(VehicleFormEvent.TiresChangeKmChanged(it.toString())) },
            label = { Text("Αλλαγή Ελαστικών (κάθε Χ km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )
        DatePickerField(label = "Αλλαγή Ελαστικών (ημερομηνία)", value = state.tiresChangeDate, onValueChange = { onEvent(VehicleFormEvent.TiresChangeDateChanged(it)) }, colors = textFieldColors)

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)

        Text("Ασφάλεια & Τέλη", style = MaterialTheme.typography.titleMedium, color = Color.Black)

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

    Box(modifier = Modifier.clickable { showDialog = true }) {
        OutlinedTextField(
            value = selectedFuelTypes,
            onValueChange = {},
            readOnly = true,
            label = { Text("Τύπος Καυσίμου") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White.copy(alpha = 0.6f),
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledLabelColor = Color.Black
            ),
            enabled = false
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Επιλογή Καυσίμου", color = Color.Black) },
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
                            Text(fuelType, modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK", color = Color.Black) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: Long?,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = Locale("el")
    val dateFormatter = SimpleDateFormat("EEEE, dd/MM/yyyy", locale)

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = Color.White.copy(alpha = 0.6f),
        disabledTextColor = Color.Black,
        disabledBorderColor = Color.Black,
        disabledLabelColor = Color.Black
    )

    Box(modifier = Modifier.clickable { showDatePicker = true }) {
        OutlinedTextField(
            value = value?.let { dateFormatter.format(Date(it)) } ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date", tint = Color.Black)
            },
            colors = customTextFieldColors,
            enabled = false // Make the text field non-editable but clickable via the Box
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value ?: System.currentTimeMillis()
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
                ) { Text("OK", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.Black) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
