package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.presentation.vehicles.VehicleFormEvent
import com.vehicleman.presentation.vehicles.VehicleFormState
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.viewmodel.AddEditVehicleViewModel
import com.vehicleman.ui.screens.components.GlassBox
import com.vehicleman.ui.screens.components.GlassTextField
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

    val contentColor = if (isNightMode) Color.White else Color(0xFF1A1A1A)
    val glassColor = if (isNightMode) Color(0xFF707070).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.92f)
    val glassBorderColor = if (isNightMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

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
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isNightMode) 70.dp else 24.dp)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { 
                        Text(
                            text = if (state.id == null) "Προσθήκη Οχήματος" else "Επεξεργασία Οχήματος", 
                            color = contentColor, 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Bold, 
                            modifier = Modifier.padding(start = 8.dp)
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(48.dp)) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_backarrow), 
                                contentDescription = "Πίσω", 
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { addEditVehicleViewModel.onEvent(VehicleFormEvent.Submit) }, modifier = Modifier.size(48.dp)) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_save), 
                                contentDescription = "Αποθήκευση", 
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECTION 1: Make & Model
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Μάρκα",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.make,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.MakeChanged(it)) },
                            placeholder = "π.χ. Toyota",
                            isNightMode = isNightMode
                        )
                    }
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Μοντέλο",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.model,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.ModelChanged(it)) },
                            placeholder = "π.χ. Corolla",
                            isNightMode = isNightMode
                        )
                    }
                }

                // SECTION 2: Plate & Year
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassBox(
                        modifier = Modifier.weight(1.2f),
                        label = "Πινακίδα",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.plateNumber,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.PlateNumberChanged(it)) },
                            placeholder = "ABC-1234",
                            isNightMode = isNightMode
                        )
                    }
                    GlassBox(
                        modifier = Modifier.weight(0.8f),
                        label = "Έτος",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.year,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.YearChanged(it)) },
                            keyboardType = KeyboardType.Number,
                            placeholder = "2020",
                            isNightMode = isNightMode
                        )
                    }
                }

                // SECTION 3: Odometer
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Χιλιόμετρα (Οδόμετρο)",
                    isNightMode = isNightMode,
                    glassColor = glassColor,
                    glassBorderColor = glassBorderColor
                ) {
                    GlassTextField(
                        value = state.currentOdometer,
                        onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.CurrentOdometerChanged(it)) },
                        keyboardType = KeyboardType.Number,
                        placeholder = "0",
                        isNightMode = isNightMode
                    )
                }

                // SECTION 4: Fuel Types
                FuelTypeSelectorGlass(
                    selectedFuelTypes = state.fuelTypes,
                    onFuelTypeChanged = { addEditVehicleViewModel.onEvent(VehicleFormEvent.FuelTypeChanged(it)) },
                    isNightMode = isNightMode,
                    glassColor = glassColor,
                    glassBorderColor = glassBorderColor
                )

                Text(
                    text = "Συντήρηση",
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Λάδια (km)",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.oilChangeKm,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.OilChangeKmChanged(it)) },
                            keyboardType = KeyboardType.Number,
                            placeholder = "κάθε Χ km",
                            isNightMode = isNightMode
                        )
                    }
                    GlassBox(
                        modifier = Modifier.weight(1.2f),
                        label = "Ημ/νία Λαδιών",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        DatePickerGlassField(
                            value = state.oilChangeDate,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.OilChangeDateChanged(it)) },
                            isNightMode = isNightMode
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Ελαστικά (km)",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.tiresChangeKm,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.TiresChangeKmChanged(it)) },
                            keyboardType = KeyboardType.Number,
                            placeholder = "κάθε Χ km",
                            isNightMode = isNightMode
                        )
                    }
                    GlassBox(
                        modifier = Modifier.weight(1.2f),
                        label = "Ημ/νία Ελαστικών",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        DatePickerGlassField(
                            value = state.tiresChangeDate,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.TiresChangeDateChanged(it)) },
                            isNightMode = isNightMode
                        )
                    }
                }

                Text(
                    text = "Ασφάλεια & Τέλη",
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Λήξη Ασφάλειας",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        DatePickerGlassField(
                            value = state.insuranceExpiryDate,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.InsuranceExpiryDateChanged(it)) },
                            isNightMode = isNightMode
                        )
                    }
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        label = "Λήξη Τελών",
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        DatePickerGlassField(
                            value = state.taxesExpiryDate,
                            onValueChange = { addEditVehicleViewModel.onEvent(VehicleFormEvent.TaxesExpiryDateChanged(it)) },
                            isNightMode = isNightMode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                state.errorMessage?.let {
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(it) {
                        snackbarHostState.showSnackbar(it)
                    }
                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}

@Composable
fun FuelTypeSelectorGlass(
    selectedFuelTypes: String,
    onFuelTypeChanged: (String) -> Unit,
    isNightMode: Boolean,
    glassColor: Color,
    glassBorderColor: Color
) {
    val fuelCategories = remember {
        linkedMapOf(
            "Ηλεκτρικό:" to listOf("Ρεύμα" to "electric"),
            "Βενζίνη:" to listOf("95" to "unleaded_95", "98" to "unleaded_98", "100" to "unleaded_100"),
            "Πετρέλαιο:" to listOf("diesel" to "diesel", "b7" to "b7"),
            "Υγραέριο (LPG):" to listOf("lpg" to "lpg", "autogas" to "autogas"),
            "Φυσικό Αέριο (CNG):" to listOf("cng" to "cng")
        )
    }

    val valueToDisplayMap = remember {
        fuelCategories.values.flatten().associate { (displayName, storedValue) -> storedValue to displayName }
    }

    var showDialog by remember { mutableStateOf(false) }

    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        label = "Τύπος Καυσίμου",
        onClick = { showDialog = true },
        isNightMode = isNightMode,
        glassColor = glassColor,
        glassBorderColor = glassBorderColor
    ) {
        val selectedDisplayNames = selectedFuelTypes
            .split(", ")
            .filter { it.isNotBlank() }
            .map { valueToDisplayMap[it] ?: it }
            .joinToString(", ")

        Box(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = if (selectedDisplayNames.isEmpty()) "Επιλογή Καυσίμου" else selectedDisplayNames,
                color = (if (isNightMode) Color.White else Color.Black).copy(alpha = if (selectedDisplayNames.isEmpty()) 0.4f else 1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Επιλογή Καυσίμου") },
            text = {
                LazyColumn {
                    fuelCategories.forEach { (category, fuels) ->
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        }
                        items(fuels) { (displayName, storedValue) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val currentTypes = selectedFuelTypes.split(", ").filter { it.isNotBlank() }.toMutableSet()
                                        if (currentTypes.contains(storedValue)) {
                                            currentTypes.remove(storedValue)
                                        } else {
                                            currentTypes.add(storedValue)
                                        }
                                        onFuelTypeChanged(currentTypes.joinToString(", "))
                                    }
                            ) {
                                Checkbox(
                                    checked = selectedFuelTypes.contains(storedValue),
                                    onCheckedChange = null
                                )
                                Text(displayName, modifier = Modifier.padding(start = 8.dp))
                            }
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
fun DatePickerGlassField(
    value: String,
    onValueChange: (String) -> Unit,
    isNightMode: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = Locale("el")
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", locale)
    val contentColor = if (isNightMode) Color.White else Color.Black

    Box(modifier = Modifier.clickable { showDatePicker = true }) {
        Box(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = value.toLongOrNull()?.let { dateFormatter.format(Date(it)) } ?: "Επιλογή",
                color = contentColor.copy(alpha = if (value.isEmpty()) 0.4f else 1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
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
                TextButton(onClick = { showDatePicker = false }) { Text("Άκυρο") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
