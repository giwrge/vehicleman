package com.vehicleman.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.domain.repositories.TranslateTitlePreference

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val translateTitlePreference by viewModel.translateTitlePreference.collectAsState()
    val showAutoReminders by viewModel.showAutoReminders.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "General",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
            Text(
                text = "When title is in Grenglish",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            val radioOptions = listOf(
                TranslateTitlePreference.ASK to "Ask to translate",
                TranslateTitlePreference.ALWAYS to "Always translate automatically",
                TranslateTitlePreference.NEVER to "Never translate"
            )
            radioOptions.forEach { (option, text) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (translateTitlePreference == option),
                            onClick = { viewModel.setTranslateTitlePreference(option) }
                        )
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (translateTitlePreference == option),
                        onClick = { viewModel.setTranslateTitlePreference(option) }
                    )
                    Text(text = text, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Show auto reminders")
            Checkbox(
                checked = showAutoReminders,
                onCheckedChange = { viewModel.setShowAutoReminders(it) }
            )
        }
    }
}
