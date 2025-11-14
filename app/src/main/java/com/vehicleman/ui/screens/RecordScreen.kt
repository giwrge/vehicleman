package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.presentation.record.RecordEvent
import com.vehicleman.presentation.record.RecordViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    navController: androidx.navigation.NavController,
    onNavigateToAddEditRecord: (String, String?) -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ιστορικό",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.state.value.selectedVehicleId?.let { vId ->
                        onNavigateToAddEditRecord(vId, "new")
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Νέα εγγραφή")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            // Background: premium road with parallax
            PremiumRoadBackground(listState = listState, isNightMode = isNightMode)

            Column(modifier = Modifier.fillMaxSize()) {
                // Vehicle folders (tabs)
                VehicleFolders(
                    vehicles = state.vehicles,
                    selectedVehicleId = state.selectedVehicleId,
                    onVehicleSelected = { viewModel.onEvent(RecordEvent.VehicleSelected(it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sticky upcoming reminder (if any)
                state.latestUpcomingReminder?.let { upcoming ->
                    StickyUpcomingReminder(
                        record = upcoming,
                        onClick = { viewModel.onEvent(RecordEvent.NavigateToEdit(upcoming.id)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Timeline list — expenses first (newest first), then reminders
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Expenses (newest first)
                    if (state.expenseRecords.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Πρόσφατα Έξοδα")
                        }
                        items(state.expenseRecords, key = { it.id }) { record ->
                            TimelineRow(
                                record = record,
                                onClick = { viewModel.onEvent(RecordEvent.ToggleExpandRecord(record.id)) },
                                onEdit = { onNavigateToAddEditRecord(record.vehicleId, record.id) },
                                onMarkCompleted = { viewModel.onEvent(RecordEvent.MarkReminderCompleted(record.id)) }
                            )
                        }
                    }

                    // Reminders section
                    if (state.reminderRecords.isNotEmpty()) {
                        item { SectionHeader(title = "Υπενθυμίσεις") }

                        items(state.reminderRecords, key = { it.id }) { record ->
                            TimelineRow(
                                record = record,
                                onClick = { viewModel.onEvent(RecordEvent.ToggleExpandRecord(record.id)) },
                                onEdit = { onNavigateToAddEditRecord(record.vehicleId, record.id) },
                                onMarkCompleted = { viewModel.onEvent(RecordEvent.MarkReminderCompleted(record.id)) }
                            )
                        }
                    }

                    // End spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

/* ---------------------------
   UI Building blocks
   --------------------------- */

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun StickyUpcomingReminder(record: Record, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3C4))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B5998)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                record.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2) }
                record.reminderDate?.let {
                    Text("Στις: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Days remaining
            record.reminderDate?.let {
                val daysLeft = ((it.time - System.currentTimeMillis()) / (1000L * 60 * 60 * 24)).coerceAtLeast(0)
                Text("${daysLeft}d", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun TimelineRow(
    record: Record,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val icon = when (record.recordType) {
        RecordType.FUEL_UP -> "⛽"
        RecordType.EXPENSE -> "🔧"
        RecordType.REMINDER -> "🔔"
    }
    val color = when (record.recordType) {
        RecordType.FUEL_UP -> Color(0xFF0D47A1)
        RecordType.EXPENSE -> Color(0xFF4E342E)
        RecordType.REMINDER -> Color(0xFF00695C)
    }

    // expansion state read from ViewModel via a local remembered toggle (safe UX)
    var expanded by remember { mutableStateOf(false) }
    // animate expansion
    val animateProgress by animateFloatAsState(if (expanded) 1f else 0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded; onClick() }
            .shadow(2.dp, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier
            .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // icon circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(brush = Brush.linearGradient(listOf(color, color.copy(alpha = 0.9f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    record.description?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(dateFormat.format(record.date), style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${record.odometer} km", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // cost / badge
                Column(horizontalAlignment = Alignment.End) {
                    if (!record.isReminder) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", record.cost ?: 0.0),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        record.recordType.takeIf { it == RecordType.FUEL_UP }?.let {
                            Text("${record.quantity ?: 0.0} lt", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        // reminder badge
                        val badgeText = if (record.isCompleted) "Ολοκληρώθηκε" else "Εκκρεμεί"
                        Text(badgeText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // actions (edit / mark)
                    Row {
                        TextButton(onClick = onEdit) { Text("Επεξεργασία") }
                        Spacer(modifier = Modifier.width(6.dp))
                        if (record.isReminder && !record.isCompleted) {
                            TextButton(onClick = onMarkCompleted) { Text("Σήμανση Ολοκλήρωσης") }
                        }
                    }
                }
            }

            // expanded area
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    record.fuelType?.let { Text("Τύπος Καυσίμου: $it", style = MaterialTheme.typography.bodySmall) }
                    record.pricePerUnit?.let { Text("Τιμή/Μονάδα: ${String.format(Locale.getDefault(), "%.3f €", it)}", style = MaterialTheme.typography.bodySmall) }
                    record.reminderDate?.let { Text("Υπενθύμιση: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

/* ---------------------------
   Vehicle Folders (premium)
   --------------------------- */
@Composable
private fun VehicleFolders(
    vehicles: List<Vehicle>,
    selectedVehicleId: String?,
    onVehicleSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(vehicles) { vehicle ->
            val isSelected = vehicle.id == selectedVehicleId
            val background = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

            Card(
                modifier = Modifier
                    .height(48.dp)
                    .clickable { onVehicleSelected(vehicle.id) },
                colors = CardDefaults.cardColors(containerColor = background),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = vehicle.name.ifEmpty { vehicle.plateNumber.ifEmpty { "Αυτοκίνητο" } }, color = textColor)
                }
            }
        }
    }
}

/* ---------------------------
   Premium Road Background
   --------------------------- */
@Composable
private fun PremiumRoadBackground(listState: LazyListState, isNightMode: Boolean) {
    val infinite = rememberInfiniteTransition()
    val anim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing))
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // background gradient
        val bg = if (isNightMode) {
            Brush.verticalGradient(listOf(Color(0xFF0F1720), Color(0xFF111827)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFEFF7F9)))
        }
        drawRect(brush = bg)

        // road center strip
        val roadWidth = w * 0.6f
        val roadLeft = (w - roadWidth) / 2f
        val roadTop = 0f
        val roadBottom = h

        // asphalt
        drawRect(color = if (isNightMode) Color(0xFF1F2937) else Color(0xFFEEEEEE),
            topLeft = Offset(roadLeft, roadTop),
            size = Size(roadWidth, roadBottom)
        )

        // dashed centerline with parallax from list scroll
        val offset = (listState.firstVisibleItemScrollOffset / 3f) + (anim * 40f)
        val dashH = 40f
        val gapH = 20f
        var y = -offset % (dashH + gapH)
        while (y < h) {
            drawRect(
                color = Color.White,
                topLeft = Offset(w / 2f - 4f, y),
                size = Size(8f, dashH)
            )
            y += dashH + gapH
        }

        // subtle side lines
        drawRect(color = Color.Gray, topLeft = Offset(roadLeft + 6f, 0f), size = Size(2f, h))
        drawRect(color = Color.Gray, topLeft = Offset(roadLeft + roadWidth - 8f, 0f), size = Size(2f, h))
    }
}
