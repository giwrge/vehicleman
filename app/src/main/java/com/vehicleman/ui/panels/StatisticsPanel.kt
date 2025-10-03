package com.vehicleman.ui.panels // ΔΙΟΡΘΩΘΗΚΕ: σε .panels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Panel 1: Statistics Screen (Charts).
 *
 * @param modifier Modifier for the layout.
 */
@Composable
fun StatisticsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Στατιστικά και Γραφήματα (Charts) θα εμφανιστούν εδώ.",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
