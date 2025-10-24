package com.vehicleman.ui.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.presentation.preference.PreferenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    navController: NavController,
    viewModel: PreferenceViewModel = hiltViewModel()
) {
    val isNightMode by viewModel.isNightMode.collectAsState()

    Scaffold {
        padding ->
        var totalDragAmount by remember { mutableStateOf(0f) }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pointerInput(Unit) {
                 detectHorizontalDragGestures(
                    onDragStart = { totalDragAmount = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragAmount += dragAmount
                    },
                    onDragEnd = {
                        if (totalDragAmount < -30) { // Swipe Left
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        ) {
            Column {
                Text(text = "Account Management")
                Text(text = "PRO Features")
                Text(text = "Subscription (Paywall)")
                Text(text = "Users (PRO)")
                Text(text = "Legal")
                Row {
                    Text(text = "Night Mode")
                    Switch(checked = isNightMode, onCheckedChange = viewModel::setNightMode)
                }
                Text(text = "Version: 1.0.0") // Placeholder
            }
        }
    }
}
