package com.vehicleman.ui.screens.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vehicleman.domain.use_case.record_ai.SuggestionSource

@Composable
fun SuggestionPill(
    source: SuggestionSource,
    modifier: Modifier = Modifier
) {
    val label = when (source) {
        SuggestionSource.RECENT_RECORD -> "RECENT"
        SuggestionSource.DOMAIN_KEYWORD -> "KEYWORD"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
