package com.vehicleman.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.presentation.records.AddEditRecordEvent // Σωστό package/event
import com.vehicleman.presentation.records.AddEditRecordViewModel // Σωστό ViewModel
import com.vehicleman.domain.model.MaintenanceRecordType // Σωστό type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Η Ευφυής Φόρμα Καταχώρησης Συμβάντων (Εξόδων/Υπενθυμίσεων).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.recordId == "new") "Νέα Καταχώρηση Συμβάντος" else "Επεξεργασία Συμβάντος") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddEditRecordEvent.OnSaveClicked) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Αποθήκευση")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... (Βασικά πεδία) ...

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateField(
                    label = "Ημερομηνία",
                    date = state.date,
                    onDateChange = { viewModel.onEvent(AddEditRecordEvent.OnDateChange(it)) },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = state.odometer,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnOdometerChange(it)) },
                    label = { Text("Χιλιόμετρα") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. SMART INPUT (Περιγραφή)
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnDescriptionChange(it)) },
                label = { Text("Περιγραφή/Smart Input") },
                placeholder = { Text("π.χ. βενζίνη 100 1.719 ή Σέρβις 100€") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. CHIP GROUP (Προτάσεις)
            Text("Προτάσεις", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.suggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { viewModel.onEvent(AddEditRecordEvent.OnSuggestionChipClicked(suggestion)) },
                        label = { Text(suggestion) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. ΠΕΔΙΑ ΔΑΠΑΝΩΝ (Δυναμική Εμφάνιση)
            if (state.showCostDetails) {
                Text("Λεπτομέρειες Δαπάνης (${state.recordType.name})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.cost,
                    onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnCostChange(it)) },
                    label = { Text("Κόστος (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.recordType == MaintenanceRecordType.FUEL_UP) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = state.quantity,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnQuantityChange(it)) },
                            label = { Text("Ποσότητα (lt)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state.pricePerUnit,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnPricePerUnitChange(it)) },
                            label = { Text("Τιμή/Μονάδα (€)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. ΠΕΔΙΑ ΥΠΕΝΘΥΜΙΣΕΩΝ (Δυναμική Εμφάνιση)
            if (state.showReminderFields || state.isReminder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ενεργοποίηση Υπενθύμισης",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Switch(
                        checked = state.isReminder,
                        onCheckedChange = {
                            if (!state.isReminderSwitchLocked) {
                                viewModel.onEvent(AddEditRecordEvent.OnToggleReminder(it))
                            }
                        },
                        enabled = !state.isReminderSwitchLocked
                    )
                }

                if (state.isReminder) {
                    Spacer(modifier = Modifier.height(16.dp))

                    DateField(
                        label = "Ημερομηνία Υπενθύμισης (Προαιρετικό)",
                        date = state.reminderDate,
                        onDateChange = { viewModel.onEvent(AddEditRecordEvent.OnReminderDateChange(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.reminderOdometer,
                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnReminderOdometerChange(it)) },
                        label = { Text("Χιλιόμετρα Υπενθύμισης (Προαιρετικό)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Εμφάνιση Σφάλματος
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
// ... (DateField Composable παραμένει ίδιο)