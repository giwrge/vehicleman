package com.vehicleman.ui.panels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordState

/**
 * Απλό / ασφαλές panel για Add/Edit Record.
 *
 * Προς το παρόν ΔΕΝ χρησιμοποιείται από την AddEditRecordScreen,
 * αλλά το κρατάμε σαν "ουδέτερο" component που δεν σπάει το build.
 *
 * Αν στο μέλλον θέλεις να σπάσουμε τη φόρμα σε panel + screen,
 * μπορούμε να το επεκτείνουμε πάνω σε αυτό.
 */
@Composable
fun AddEditRecordPanel(
    state: AddEditRecordState,
    onEvent: (AddEditRecordEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(AddEditRecordEvent.TitleChanged(it)) },
            label = { Text("Τίτλος") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(AddEditRecordEvent.DescriptionChanged(it)) },
            label = { Text("Περιγραφή") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            maxLines = 4
        )

        // Αν θέλεις, εδώ μπορείς να προσθέσεις κι άλλα πεδία αργότερα
    }
}
