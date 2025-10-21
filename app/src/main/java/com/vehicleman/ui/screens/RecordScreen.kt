/*package com.vehicleman.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.presentation.records.RecordViewModel
import com.vehicleman.domain.model.MaintenanceRecord
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    vehicleId: String,
    onNavigateHome: () -> Unit,
    onNavigateToAddEditRecord: (String) -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ιστορικό Οχήματος", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEditRecord(vehicleId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Νέα Εγγραφή")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // 🔹 Swipe navigation (Record ↔ Statistics / Preferences)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        val dx = dragAmount
                        val dy = change.positionChange().y
                        // Έλεγχος: swipe μόνο αν είναι καθαρά οριζόντιο
                        if (abs(dx) > abs(dy) * 2) {
                            if (dx > 0) {
                                onNavigateToPreferences() // swipe right
                            } else {
                                onNavigateToStatistics()  // swipe left
                            }
                        }
                    }
                }
        ) {
            AnimatedRoadBackground(scrollState)

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Sticky Reminder (πάνω στο "οδόστρωμα")
                state.latestReminder?.let { reminder ->
                    stickyHeader {
                        ReminderStickyCard(reminder)
                    }
                }

                // Εγγραφές εξόδων (expenses)
                if (state.expenseRecords.isNotEmpty()) {
                    item {
                        Text(
                            "Πρόσφατα Έξοδα",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(state.expenseRecords) { record ->
                        ExpenseRecordCard(record)
                    }
                }

                // Υπενθυμίσεις (reminders)
                if (state.reminderRecords.isNotEmpty()) {
                    item {
                        Text(
                            "Υπενθυμίσεις",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(state.reminderRecords) { reminder ->
                        ReminderCard(reminder)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRecordCard(record: MaintenanceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(record.description, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Ημερομηνία: ${record.date}")
            Text("Κόστος: ${record.cost} €", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ReminderCard(reminder: MaintenanceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF6FF))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Υπενθύμιση: ${reminder.description}", fontWeight = FontWeight.Bold)
            Text("Ημερομηνία: ${reminder.date}")
            reminder.reminderOdometer?.let {
                Text("Χιλιόμετρα: $it km")
            }
        }
    }
}

@Composable
fun ReminderStickyCard(reminder: MaintenanceRecord) {
    Surface(
        color = Color(0xFFFFF9C4),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📌 Επόμενη Υπενθύμιση", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(reminder.description)
            Text("Ημερομηνία: ${reminder.date}")
        }
    }
}

/**
 * Κινούμενο "οδόστρωμα" στο φόντο.
 */
@Composable
fun AnimatedRoadBackground(scrollState: LazyListState) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val offset = (scrollState.firstVisibleItemScrollOffset + animationOffset) % 40
        val lineWidth = size.width / 2f
        for (y in -40..size.height.toInt() step 40) {
            val patternType = when ((y / 40 + scrollState.firstVisibleItemIndex) % 3) {
                0 -> "solid"
                1 -> "double"
                else -> "dashed"
            }
            val yPos = y.toFloat() + offset
            drawRoadLine(lineWidth, yPos, patternType)
        }
    }
}

fun DrawScope.drawRoadLine(width: Float, y: Float, type: String) {
    val centerX = size.width / 2
    when (type) {
        "solid" -> drawLine(
            color = Color.Gray,
            start = Offset(centerX - width / 2, y),
            end = Offset(centerX + width / 2, y),
            strokeWidth = 6f
        )
        "double" -> {
            drawLine(Color.Gray, Offset(centerX - width / 2, y - 4), Offset(centerX + width / 2, y - 4), 4f)
            drawLine(Color.Gray, Offset(centerX - width / 2, y + 4), Offset(centerX + width / 2, y + 4), 4f)
        }
        "dashed" -> {
            val dashWidth = 40f
            var x = centerX - width / 2
            while (x < centerX + width / 2) {
                drawLine(Color.LightGray, Offset(x, y), Offset(x + dashWidth / 2, y), 6f)
                x += dashWidth
            }
        }
    }
}
*/