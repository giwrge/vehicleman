package com.vehicleman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordViewModel
import com.vehicleman.presentation.record.mapCategoryToIcon
import com.vehicleman.domain.use_case.record_ai.SuggestionItem
import com.vehicleman.domain.use_case.record_ai.SuggestionSource
import com.vehicleman.ui.screens.components.SuggestionPill
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    navController: NavController,
    isNightMode: Boolean,
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar για errors
    LaunchedEffect(state.errorMessage) {
        val msg = state.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.onEvent(AddEditRecordEvent.ErrorShown)
        }
    }

    // Navigation μετά το save
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(AddEditRecordEvent.NavigateBackConsumed)
        }
    }

    val scrollState = rememberScrollState()

    // Calendar state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // *** ΑΥΤΟΜΑΤΗ ΕΠΙΒΕΒΑΙΩΣΗ ΗΜΕΡΟΜΗΝΙΑΣ ***
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            viewModel.onEvent(AddEditRecordEvent.DateChanged(Date(it)))
            showDatePicker = false
        }
    }

    if (state.showTranslateTitleDialog) {
        var dontAskAgain by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = false, dontAskAgain = false)) },
            title = { Text("Μετάφραση Τίτλου") },
            text = { 
                Column {
                    Text("Ο τίτλος φαίνεται να είναι γραμμένος σε Grenglish. Θέλετε να τον μεταφράσουμε στα Ελληνικά;") 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dontAskAgain, onCheckedChange = { dontAskAgain = it })
                        Text("Να μην ερωτηθώ ξανά")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = true, dontAskAgain = dontAskAgain)) }) {
                    Text("Ναι")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = false, dontAskAgain = dontAskAgain)) }) {
                    Text("Όχι")
                }
            }
        )
    }

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
                onClick = { viewModel.onEvent(AddEditRecordEvent.Save) }
            ) {
                Text("Αποθήκευση")
            }
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

                // ---------------------------------------------------------------
                // ΤΙΤΛΟΣ - AI Driven
                // ---------------------------------------------------------------
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.TitleChanged(it)) },
                    label = { Text("Τίτλος") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ---------------------------------------------------------------
                // ΗΜΕΡΟΜΗΝΙΑ - Calendar Chip επάνω δεξιά
                // ---------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AssistChip(
                        onClick = { showDatePicker = true },
                        label = {
                            Text(
                                text = if (state.dateText.isBlank()) "Ημ/νία"
                                else state.dateText
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Ημερολόγιο"
                            )
                        }
                    )
                }

                // Full month calendar ως dialog
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = { }, // ΑΦΑΙΡΕΘΗΚΕ ΤΟ ΟΚ
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Άκυρο")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                // ---------------------------------------------------------------
                // SUGGESTIONS - σειρές (όχι buttons)
                // ---------------------------------------------------------------

                if (state.suggestions.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.suggestions.take(8).forEach { item ->
                            Surface(
                                tonalElevation = 2.dp,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onEvent(
                                            AddEditRecordEvent.SuggestionClicked(item)
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // ---- Text (left)
                                    Text(
                                        text = item.text,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    // ---- Pill (right)
                                    SuggestionPill(source = item.source)
                                }
                            }
                        }
                    }
                }
                    // -----------------------------------------------------------
                    // Km (μόνο για Expense / Fuel)
                    // -----------------------------------------------------------
                    if (state.recordType != RecordType.REMINDER) {
                        OutlinedTextField(
                            value = state.odometer,
                            onValueChange = {
                                viewModel.onEvent(AddEditRecordEvent.OdometerChanged(it))
                            },
                            label = { Text("Χιλιόμετρα") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    // -----------------------------------------------------------
                    // Κόστος (μόνο Expense / Fuel)
                    // -----------------------------------------------------------
                    if (!state.isFutureDate) {
                        OutlinedTextField(
                            value = state.cost,
                            onValueChange = {
                                viewModel.onEvent(AddEditRecordEvent.CostChanged(it))
                            },
                            label = { Text("Κόστος (€)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    // -----------------------------------------------------------
                    // Πεδία καυσίμων
                    // -----------------------------------------------------------
                    if (state.recordType == RecordType.FUEL_UP) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = state.quantity,
                                onValueChange = {
                                    viewModel.onEvent(AddEditRecordEvent.QuantityChanged(it))
                                },
                                label = { Text("Ποσότητα (lt)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = state.pricePerUnit,
                                onValueChange = {
                                    viewModel.onEvent(AddEditRecordEvent.PricePerUnitChanged(it))
                                },
                                label = { Text("€/lt") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = state.fuelTypeText,
                            onValueChange = {
                                viewModel.onEvent(AddEditRecordEvent.FuelTypeChanged(it))
                            },
                            label = { Text("Τύπος καυσίμου") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // -----------------------------------------------------------
                    // Πεδία ΥΠΕΝΘΥΜΙΣΗΣ
                    // -----------------------------------------------------------
                    if (state.recordType == RecordType.REMINDER) {

                        OutlinedTextField(
                            value = state.reminderDateText,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditRecordEvent.ReminderDateTextChanged(it)
                                )
                            },
                            label = { Text("Ημερομηνία υπενθύμισης") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = state.reminderOdometer,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditRecordEvent.ReminderOdometerChanged(it)
                                )
                            },
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
                                onCheckedChange = {
                                    viewModel.onEvent(AddEditRecordEvent.ToggleCompleted)
                                }
                            )
                            Text("Ολοκληρώθηκε")
                        }
                    }
                    // -----------------------------------------------------------
                    // Πεδία ΚΟΣΤΟΣ ΥΠΕΝΘΥΜΙΣΗΣ
                    // -----------------------------------------------------------
                    if (state.isFutureDate) {
                        OutlinedTextField(
                            value = state.costReminder,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.CostReminderChanged(it)) },
                            label = { Text("Κόστος υπενθύμισης (€)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    // -----------------------------------------------------------
                    // ΠΕΡΙΓΡΑΦΗ (AI μπορεί να τη συμπληρώσει)
                    // -----------------------------------------------------------
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = {
                            viewModel.onEvent(AddEditRecordEvent.DescriptionChanged(it))
                        },
                        label = { Text("Περιγραφή (προαιρετικό)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        maxLines = 5
                    )

                    // -----------------------------------------------------------
                    // CATEGORY PREVIEW (από RecordCategorizerUseCase)
                    // -----------------------------------------------------------
                    state.category?.let { category ->
                        CategoryPreview(category = category)
                    }
                }
            }
        }
    }

@Composable
private fun SuggestionPill(source: SuggestionSource) {
    val (text, bgColor, textColor) = when (source) {
        SuggestionSource.RECENT_RECORD -> Triple(
            "RECENT",
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.primary
        )
        SuggestionSource.DOMAIN_KEYWORD -> Triple(
            "SMART",
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.secondary
        )
    }

    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun CategoryPreview(category: RecordCategory) {
    val iconRes = mapCategoryToIcon(category)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Κατηγορία",
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = "Αυτόματη κατηγορία: ${category::class.simpleName ?: "Άγνωστη"}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

