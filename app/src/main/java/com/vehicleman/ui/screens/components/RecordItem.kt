package com.vehicleman.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vehicleman.domain.model.Record
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RecordItem(
    record: Record,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = record.title)
            Text(text = "Odometer: ${record.odometer} km")
            record.cost?.let {
                Text(text = "Amount: $it")
            }
            Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(record.date))
        }
    }
}
