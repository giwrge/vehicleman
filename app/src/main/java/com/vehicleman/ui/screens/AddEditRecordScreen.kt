package com.vehicleman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordViewModel
import com.vehicleman.presentation.record.mapCategoryToIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    navController: NavController,
    isNightMode: Boolean,
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Σnackbar για error messages
    LaunchedEffect(state.errorMessage) {
        val msg = state.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onEvent(AddEditRecordEvent.ErrorShown)
        }
    }

    // Navigation back όταν γίνει save
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(AddEditRecordEvent.NavigateBackConsumed)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isNew) "Νέα εγγραφή"
                        else "Επεξεργασία εγγραφής"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(AddEditRecordEvent.Save) },
                text = {
                    Text("Αποθήκευση")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,   // Βάλε ό,τι θέλεις
                        contentDescription = null
                    )
                }
            )


        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Transparent)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                // Τύπος εγγραφής
                RecordTypeSegmentedControl(
                    selectedType = state.recordType,
                    onTypeSelected = { viewModel.onEvent(AddEditRecordEvent.RecordTypeChanged(it)) }
                )

                Spacer(Modifier.height(8.dp))

                // Τίτλος
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.TitleChanged(it)) },
                    label = { Text("Τίτλος") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Ημερομηνία
                OutlinedTextField(
                    value = state.dateText,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.DateTextChanged(it)) },
                    label = { Text("Ημερομηνία (ΗΗ/ΜΜ/ΕΕΕΕ)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Km (για έξοδα/καύσιμα)
                if (state.recordType != RecordType.REMINDER) {
                    OutlinedTextField(
                        value = state.odometer,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.OdometerChanged(it)) },
                        label = { Text("Χιλιόμετρα") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                // Κόστος
                if (state.recordType != RecordType.REMINDER) {
                    OutlinedTextField(
                        value = state.cost,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.CostChanged(it)) },
                        label = { Text("Κόστος (€)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                // Πεδία για FUEL_UP
                if (state.recordType == RecordType.FUEL_UP) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.quantity,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.QuantityChanged(it)) },
                            label = { Text("Ποσότητα (lt)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.pricePerUnit,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.PricePerUnitChanged(it)) },
                            label = { Text("€/lt") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = state.fuelTypeText,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.FuelTypeChanged(it)) },
                        label = { Text("Τύπος καυσίμου (π.χ. unleaded_95)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Πεδία για REMINDER
                if (state.recordType == RecordType.REMINDER) {
                    OutlinedTextField(
                        value = state.reminderDateText,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.ReminderDateTextChanged(it)) },
                        label = { Text("Ημερομηνία υπενθύμισης (ΗΗ/ΜΜ/ΕΕΕΕ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.reminderOdometer,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.ReminderOdometerChanged(it)) },
                        label = { Text("Υπενθύμιση στα χιλιόμετρα (προαιρετικό)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Checkbox(
                            checked = state.isCompleted,
                            onCheckedChange = { viewModel.onEvent(AddEditRecordEvent.ToggleCompleted) }
                        )
                        Text("Ολοκληρώθηκε")
                    }
                }

                // Περιγραφή
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.DescriptionChanged(it)) },
                    label = { Text("Περιγραφή (προαιρετικό)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    maxLines = 5
                )

                // Κατηγορία (από τον super-categorizer)
                state.category?.let { category ->
                    CategoryPreview(category = category)
                }
            }
        }
    }
}

@Composable
private fun RecordTypeSegmentedControl(
    selectedType: RecordType,
    onTypeSelected: (RecordType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TypeChip(
            label = "Έξοδο",
            selected = selectedType == RecordType.EXPENSE,
            onClick = { onTypeSelected(RecordType.EXPENSE) }
        )
        TypeChip(
            label = "Καύσιμα",
            selected = selectedType == RecordType.FUEL_UP,
            onClick = { onTypeSelected(RecordType.FUEL_UP) }
        )
        TypeChip(
            label = "Υπενθύμιση",
            selected = selectedType == RecordType.REMINDER,
            onClick = { onTypeSelected(RecordType.REMINDER) }
        )
    }
}

@Composable
private fun TypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            labelColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun CategoryPreview(category: RecordCategory) {
    val iconRes = mapCategoryToIcon(category)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(top = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Κατηγορία",
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = "Αυτόματη κατηγορία: ${category::class.simpleName ?: "Άγνωστη"}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
