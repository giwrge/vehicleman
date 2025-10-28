package com.vehicleman.ui.panels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordPanel(
    state: AddEditRecordState,
    onEvent: (AddEditRecordEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showReminderDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(AddEditRecordEvent.OnDescriptionChange(it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            state.suggestions.forEach { suggestion ->
                AssistChip(
                    onClick = { onEvent(AddEditRecordEvent.OnSuggestionChipClicked(suggestion)) },
                    label = { Text(suggestion) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // Date Picker Field
        OutlinedTextField(
            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(state.date),
            onValueChange = {},
            label = { Text("Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.date.time)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onEvent(AddEditRecordEvent.OnDateChange(Date(it)))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(
            value = state.odometer,
            onValueChange = { onEvent(AddEditRecordEvent.OnOdometerChange(it)) },
            label = { Text("Odometer") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (state.showCostDetails) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.cost,
                    onValueChange = { onEvent(AddEditRecordEvent.OnCostChange(it)) },
                    label = { Text("Cost") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.quantity,
                    onValueChange = { onEvent(AddEditRecordEvent.OnQuantityChange(it)) },
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.pricePerUnit,
                    onValueChange = { onEvent(AddEditRecordEvent.OnPricePerUnitChange(it)) },
                    label = { Text("Price/Unit") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        } else {
            OutlinedTextField(
                value = state.cost,
                onValueChange = { onEvent(AddEditRecordEvent.OnCostChange(it)) },
                label = { Text("Cost") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        if (state.showFuelTypeSelection) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.vehicleFuelTypes.forEach { fuelType ->
                    FilterChip(
                        selected = state.selectedFuelType == fuelType,
                        onClick = { onEvent(AddEditRecordEvent.OnFuelTypeSelected(fuelType)) },
                        label = { Text(fuelType) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Set Reminder")
            Switch(
                checked = state.isReminder,
                onCheckedChange = { onEvent(AddEditRecordEvent.OnToggleReminder(it)) },
                enabled = !state.isReminderSwitchLocked
            )
        }

        if (state.showReminderFields) {
            Column {
                // Reminder Date Picker
                OutlinedTextField(
                    value = state.reminderDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Reminder Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReminderDatePicker = true },
                    enabled = false
                )

                if (showReminderDatePicker) {
                    val reminderDatePickerState = rememberDatePickerState(initialSelectedDateMillis = state.reminderDate?.time)
                    DatePickerDialog(
                        onDismissRequest = { showReminderDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                reminderDatePickerState.selectedDateMillis?.let {
                                    onEvent(AddEditRecordEvent.OnReminderDateChange(Date(it)))
                                }
                                showReminderDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = { TextButton(onClick = { showReminderDatePicker = false }) { Text("Cancel") } }
                    ) {
                        DatePicker(state = reminderDatePickerState)
                    }
                }

                OutlinedTextField(
                    value = state.reminderOdometer,
                    onValueChange = { onEvent(AddEditRecordEvent.OnReminderOdometerChange(it)) },
                    label = { Text("Reminder at Odometer") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
