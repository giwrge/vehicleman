package com.vehicleman.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.domain.model.RecordType
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect // Προσθήκη αυτού του import

/**
 * Η Ευφυής Φόρμα Καταχώρησης Συμβάντων (Εξόδων/Υπενθυμίσεων).
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    // ΑΛΛΑΓΗ 1: Η οθόνη δέχεται δύο "οδηγίες" αντί για μία.
    onRecordSaved: () -> Unit,      // Τι να κάνει ΜΕΤΑ την επιτυχή αποθήκευση.
    onNavigateBack: () -> Unit,      // Τι να κάνει σε απλή επιστροφή/ακύρωση.
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // ΑΛΛΑΓΗ 2: Προσθέτουμε έναν "κατάσκοπο" (LaunchedEffect).
    // Αυτός ο κώδικας θα τρέξει ΜΟΝΟ όταν η τιμή του state.isSaveSuccess αλλάξει από false σε true.
    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            onRecordSaved() // Εκτελούμε την οδηγία για επιτυχή αποθήκευση.
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.recordId == "new") "Νέα Καταχώρηση" else "Επεξεργασία") },
                navigationIcon = {
                    // ΑΛΛΑΓΗ 3: Το βέλος "πίσω" καλεί ΠΑΝΤΑ την απλή οδηγία επιστροφής.
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Πίσω")
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

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddEditRecordEvent.OnDescriptionChange(it)) },
                label = { Text("Περιγραφή/Smart Input") },
                placeholder = { Text("π.χ. βενζίνη 100 1.719 ή Σέρβις 100€") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

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

                if (state.recordType == RecordType.FUEL_UP) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    date: Date?,
    onDateChange: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    date?.let {
        calendar.time = it
    }
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate by remember(date) { derivedStateOf { date?.let { dateFormat.format(it) } ?: "" } }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val newCalendar = java.util.Calendar.getInstance()
            newCalendar.set(selectedYear, selectedMonth, selectedDay)
            onDateChange(newCalendar.time)
        }, year, month, day
    )

    OutlinedTextField(
        value = formattedDate,
        onValueChange = { /* Read-only, updated via dialog */ },
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = modifier
    )
}
