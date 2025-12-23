package com.vehicleman.ui.screens.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vehicleman.domain.use_case.record_ai.SuggestionSource

@Composable
fun SuggestionPill(source: SuggestionSource) {
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
