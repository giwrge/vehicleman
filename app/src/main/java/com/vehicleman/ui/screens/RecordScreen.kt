package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
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
    navController: NavController,
    onNavigateToAddEditRecord: (String, String?) -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
    vehicleId: String?,
    isNightMode: Boolean
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var recentlyDeleted by remember { mutableStateOf<Record?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ιστορικό",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        // μπορείς να βάλεις δικό σου back icon εδώ αν θες
                        Text("<")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // 1η προτεραιότητα: selectedVehicleId από state
                    val vId = state.selectedVehicleId
                        ?: vehicleId
                        ?: viewModel.vehicleId

                    if (vId != null) {
                        onNavigateToAddEditRecord(vId, "new")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Νέα εγγραφή"
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PremiumRoadBackground(listState = listState, isNightMode = isNightMode)

            Column(modifier = Modifier.fillMaxSize()) {

                VehicleFolders(
                    vehicles = state.vehicles,
                    selectedVehicleId = state.selectedVehicleId,
                    onVehicleSelected = { viewModel.onEvent(RecordEvent.VehicleSelected(it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sticky upcoming reminder (πιο κοντινή μελλοντική)
                state.latestUpcomingReminder?.let { upcoming ->
                    StickyUpcomingReminder(
                        record = upcoming,
                        isNightMode = isNightMode,
                        onClick = {
                            viewModel.onEvent(RecordEvent.NavigateToEdit(upcoming.id))
                            onNavigateToAddEditRecord(upcoming.vehicleId, upcoming.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (state.reminderRecords.isNotEmpty()) {
                        item { SectionHeader(title = "Υπενθυμίσεις") }

                        items(state.reminderRecords, key = { it.id }) { record ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value: SwipeToDismissBoxValue ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        scope.launch {
                                            recentlyDeleted = record
                                            viewModel.deleteRecord(record)
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Η υπενθύμιση διαγράφηκε",
                                                actionLabel = "ΑΝΑΙΡΕΣΗ"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                recentlyDeleted?.let { viewModel.saveRecord(it) }
                                            }
                                        }
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = { DeleteBackground() },
                                enableDismissFromEndToStart = true,
                                enableDismissFromStartToEnd = false
                            ) {
                                TimelineRow(
                                    record = record,
                                    isNightMode = isNightMode,
                                    onClick = {
                                        viewModel.onEvent(
                                            RecordEvent.ToggleExpandRecord(
                                                record.id
                                            )
                                        )
                                    },
                                    onEdit = {
                                        onNavigateToAddEditRecord(record.vehicleId, record.id)
                                    },
                                    onMarkCompleted = {
                                        viewModel.onEvent(
                                            RecordEvent.MarkReminderCompleted(
                                                record.id
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (state.expenseRecords.isNotEmpty()) {
                        item { SectionHeader(title = "Πρόσφατα έξοδα") }

                        items(state.expenseRecords, key = { it.id }) { record ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value: SwipeToDismissBoxValue ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        scope.launch {
                                            recentlyDeleted = record
                                            viewModel.deleteRecord(record)
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Η εγγραφή διαγράφηκε",
                                                actionLabel = "ΑΝΑΙΡΕΣΗ"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                recentlyDeleted?.let { viewModel.saveRecord(it) }
                                            }
                                        }
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = { DeleteBackground() },
                                enableDismissFromEndToStart = true,
                                enableDismissFromStartToEnd = false
                            ) {
                                TimelineRow(
                                    record = record,
                                    isNightMode = isNightMode,
                                    onClick = {
                                        viewModel.onEvent(
                                            RecordEvent.ToggleExpandRecord(
                                                record.id
                                            )
                                        )
                                    },
                                    onEdit = {
                                        onNavigateToAddEditRecord(record.vehicleId, record.id)
                                    },
                                    onMarkCompleted = { /* όχι εδώ */ }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

/* ------------------------- UI βοηθητικά -------------------------- */

@Composable
private fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB71C1C))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = "Διαγραφή",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

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
private fun StickyUpcomingReminder(
    record: Record,
    isNightMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val bgColor = if (isNightMode) Color(0xFFFFD87A) else Color(0xFFFFF3C4)

    Card(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Text(
                    record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                record.description?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
                record.reminderDate?.let {
                    Text("Στις: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }

            record.reminderDate?.let {
                val daysLeft =
                    ((it.time - System.currentTimeMillis()) / (1000L * 60 * 60 * 24)).coerceAtLeast(0)
                Text(
                    "${daysLeft}d",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(
    record: Record,
    isNightMode: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val cardColor = MaterialTheme.colorScheme.surface.copy(
        alpha = if (isNightMode) 0.60f else 0.75f
    )

    val (iconRes, tint) = remember(record.recordType, record.fuelType, isNightMode) {
        mapRecordToIcon(record, isNightMode)
    }

    var expanded by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.0f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "expand_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                onClick()
            }
            .shadow(2.dp, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    tint,
                                    tint.copy(alpha = 0.9f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        record.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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

                Column(horizontalAlignment = Alignment.End) {
                    if (!record.isReminder) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", record.cost ?: 0.0),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (record.recordType == RecordType.FUEL_UP) {
                            Text(
                                "${record.quantity ?: 0.0} lt",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        val badgeText =
                            if (record.isCompleted) "Ολοκληρώθηκε" else "Εκκρεμεί"
                        Text(
                            badgeText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        TextButton(onClick = onEdit) {
                            Text("Επεξεργασία")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        if (record.isReminder && !record.isCompleted) {
                            TextButton(onClick = onMarkCompleted) {
                                Text("Ολοκλήρωση")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .alpha(alphaAnim)
                ) {
                    record.fuelType?.let {
                        Text(
                            "Τύπος καυσίμου: $it",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    record.pricePerUnit?.let {
                        Text(
                            "Τιμή/Μονάδα: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%.3f €",
                                    it
                                )
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    record.reminderDate?.let {
                        Text(
                            "Υπενθύμιση: ${dateFormat.format(it)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/* --------- Vehicle folders: brand + model + plate --------- */

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
            val background =
                if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            val textColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

            Card(
                modifier = Modifier
                    .height(56.dp)
                    .clickable { onVehicleSelected(vehicle.id) },
                colors = CardDefaults.cardColors(containerColor = background),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${vehicle.make} ${vehicle.model}",
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                    Text(
                        text = vehicle.plateNumber,
                        color = textColor.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

/* ---------------------- Premium Road Background ---------------------- */

@Composable
private fun PremiumRoadBackground(listState: LazyListState, isNightMode: Boolean) {
    val infinite = rememberInfiniteTransition(label = "road_anim")
    val anim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing)
        ),
        label = "road_offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val bg = if (isNightMode) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF0F1720),
                    Color(0xFF111827)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFF7FBFF),
                    Color(0xFFEFF7F9)
                )
            )
        }
        drawRect(brush = bg)

        val roadWidth = w * 0.6f
        val roadLeft = (w - roadWidth) / 2f

        drawRect(
            color = if (isNightMode) Color(0xFF1F2937) else Color(0xFFEEEEEE),
            topLeft = Offset(roadLeft, 0f),
            size = Size(roadWidth, h)
        )

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

        drawRect(
            color = Color.Gray,
            topLeft = Offset(roadLeft + 6f, 0f),
            size = Size(2f, h)
        )
        drawRect(
            color = Color.Gray,
            topLeft = Offset(roadLeft + roadWidth - 8f, 0f),
            size = Size(2f, h)
        )
    }
}

/* ---------------------- Icon mapping helper ---------------------- */

private fun mapRecordToIcon(
    record: Record,
    isNightMode: Boolean
): Pair<Int, Color> {
    val baseTint = when (record.recordType) {
        RecordType.FUEL_UP -> Color(0xFF0D47A1)
        RecordType.EXPENSE -> Color(0xFF4E342E)
        RecordType.REMINDER -> Color(0xFF00695C)
    }

    val iconRes = when (record.recordType) {
        RecordType.FUEL_UP -> {
            when (record.fuelType?.lowercase(Locale.ROOT)) {
                "ρεύμα" ->
                    if (isNightMode) R.drawable.ic_fuel_electric_filled else R.drawable.ic_fuel_electric_outline
                "unleaded_95", "unleaded_98", "unleaded_100" ->
                    if (isNightMode) R.drawable.ic_fuel_petrol_filled else R.drawable.ic_fuel_petrol_outline
                "diesel", "b7" ->
                    if (isNightMode) R.drawable.ic_fuel_diesel_filled else R.drawable.ic_fuel_diesel_outline
                "lpg", "autogas" ->
                    if (isNightMode) R.drawable.ic_fuel_lpg_filled else R.drawable.ic_fuel_lpg_outline
                "cng" ->
                    if (isNightMode) R.drawable.ic_fuel_cng_filled else R.drawable.ic_fuel_cng_outline
                else ->
                    if (isNightMode) R.drawable.ic_fuel_generic_filled else R.drawable.ic_fuel_generic_outline
            }
        }

        RecordType.EXPENSE -> {
            if (isNightMode) R.drawable.ic_expense_filled else R.drawable.ic_expense_outline
        }

        RecordType.REMINDER -> {
            if (isNightMode) {
                if (record.isCompleted) R.drawable.ic_reminder_completed_filled
                else R.drawable.ic_reminder_filled
            } else {
                if (record.isCompleted) R.drawable.ic_reminder_completed_outline
                else R.drawable.ic_reminder_outline
            }
        }
    }

    return iconRes to baseTint
}
