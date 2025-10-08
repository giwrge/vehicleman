package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack // <--- ΑΥΤΟ ΤΟ IMPORT ΕΛΕΙΠΕ
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Οθόνη που εμφανίζει τη Λίστα Συμβάντων (Εξόδων/Υπενθυμίσεων) για ένα συγκεκριμένο όχημα.
 *
 * @param vehicleId Το ID του οχήματος.
 * @param onNavigateBack Callback για επιστροφή.
 * @param onNavigateToEntryForm Callback για πλοήγηση στην Ευφυή Φόρμα.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryListScreen(
    vehicleId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEntryForm: (entryId: String?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Συμβάντα Οχήματος $vehicleId") }, // Προσωρινός τίτλος
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToEntryForm(null) }, // Tap για νέα καταχώρηση
                icon = { Icon(Icons.Filled.Add, contentDescription = "Προσθήκη Συμβάντος") },
                text = { Text("ΣΥΜΒΑΝ") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Λίστα Συμβάντων (Εκκρεμεί Υλοποίηση)\n" +
                        "Εδώ θα εμφανίζεται ο Οδικός Χάρτης με τις Υπενθυμίσεις/Δαπάνες.",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}