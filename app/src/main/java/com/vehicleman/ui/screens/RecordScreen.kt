package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.vehicleman.ui.navigation.NavDestinations
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    navController: NavController,
    onNavigateToAddEditRecord: (String, String?) -> Unit,
    onNavigateToStatistics: (String) -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToProMode: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var recentlyDeleted by remember { mutableStateOf<Record?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onScreenHidden()
            viewModel.saveScrollState(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }

    LaunchedEffect(state.timelineItems) {
        if (state.timelineItems.isNotEmpty()) {
            if (viewModel.shouldResetScroll()) {
                listState.animateScrollToItem(index = state.initialScrollIndex)
            } else {
                listState.scrollToItem(viewModel.savedScrollIndex, viewModel.savedScrollOffset)
            }
        }
    }

    val showStickyReminder by remember {
        derivedStateOf {
            val sticky = state.latestUpcomingReminder ?: return@derivedStateOf false
            val firstVisibleIndex = listState.firstVisibleItemIndex
            if (firstVisibleIndex < 0 || state.timelineItems.isEmpty()) return@derivedStateOf true
            val firstVisibleRecord = state.timelineItems.getOrNull(firstVisibleIndex) ?: return@derivedStateOf true
            val firstVisibleDate = if (firstVisibleRecord.isReminder) firstVisibleRecord.reminderDate else firstVisibleRecord.date
            (firstVisibleDate != null && sticky.reminderDate != null) && firstVisibleDate.before(sticky.reminderDate)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            RecordScreenTopAppBar(
                vehicles = state.vehicles,
                selectedVehicleId = state.selectedVehicleId,
                onVehicleSelected = { viewModel.onEvent(RecordEvent.VehicleSelected(it)) },
                onNavigateToHome = { navController.navigate(NavDestinations.HOME_ROUTE) { popUpTo(NavDestinations.HOME_ROUTE) { inclusive = true } } },
                onNavigateToStatistics = { state.selectedVehicleId?.let { onNavigateToStatistics(it) } },
                onNavigateToPreferences = onNavigateToPreferences,
                onNavigateToProMode = onNavigateToProMode,
                onNavigateToSignUp = onNavigateToSignUp
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                state.selectedVehicleId?.let { onNavigateToAddEditRecord(it, "new") }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Νέα εγγραφή")
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            PremiumRoadBackground(listState = listState, isNightMode = isNightMode) // You need to create this

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = showStickyReminder,
                    enter = slideInVertically { -it },
                    exit = slideOutVertically { -it }
                ) {
                    state.latestUpcomingReminder?.let {
                        StickyUpcomingReminder(
                            record = it,
                            isNightMode = isNightMode,
                            onClick = { onNavigateToAddEditRecord(it.vehicleId, it.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
                    items(state.timelineItems, key = { it.id }) { record ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    scope.launch {
                                        recentlyDeleted = record
                                        viewModel.deleteRecord(record)
                                        val result = snackbarHostState.showSnackbar("Η εγγραφή διαγράφηκε", "ΑΝΑΙΡΕΣΗ")
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
                                onEdit = { onNavigateToAddEditRecord(record.vehicleId, record.id) },
                                onMarkCompleted = { viewModel.onEvent(RecordEvent.MarkReminderCompleted(record.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordScreenTopAppBar(
    vehicles: List<Vehicle>,
    selectedVehicleId: String?,
    onVehicleSelected: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToProMode: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var vehicleMenuExpanded by remember { mutableStateOf(false) }
    val selectedVehicle = vehicles.find { it.id == selectedVehicleId }

    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier.clickable { vehicleMenuExpanded = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedVehicle?.let { "${it.make} ${it.model}" } ?: "Επιλογή Οχήματος")
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select vehicle")
                }
                DropdownMenu(
                    expanded = vehicleMenuExpanded,
                    onDismissRequest = { vehicleMenuExpanded = false }
                ) {
                    vehicles.forEach { vehicle ->
                        DropdownMenuItem(text = { Text("${vehicle.make} ${vehicle.model}") }, onClick = {
                            onVehicleSelected(vehicle.id)
                            vehicleMenuExpanded = false
                        })
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateToHome) {
                Icon(painter = painterResource(R.mipmap.ic_home), contentDescription = "Home")
            }
        },
        actions = {
            IconButton(onClick = onNavigateToStatistics) {
                Icon(painter = painterResource(R.mipmap.ic_statistic_bar), contentDescription = "Statistics")
            }
            // Icons from HomeScreen
            IconButton(onClick = onNavigateToSignUp) {
                Icon(painter = painterResource(R.mipmap.ic_sing_up), contentDescription = "Sign Up", modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = onNavigateToProMode) {
                Icon(painter = painterResource(R.mipmap.ic_promode_vip), contentDescription = "Pro Mode", modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = onNavigateToPreferences) {
                Icon(painter = painterResource(R.mipmap.ic_settings), contentDescription = "Settings", modifier = Modifier.size(32.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
    val bgColor = if (isNightMode) Color(0xFF1A237E).copy(alpha = 0.7f) else Color(0xFFE3F2FD).copy(alpha = 0.7f)

    Card(
        modifier = modifier.padding(horizontal = 12.dp).shadow(6.dp, RoundedCornerShape(12.dp)).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF3B5998)), contentAlignment = Alignment.Center) {
                Text("🔔", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                record.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2) }
                record.reminderDate?.let { Text("Στις: ${dateFormat.format(it)}", style = MaterialTheme.typography.bodySmall) }
            }
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
    isNightMode: Boolean,
    onEdit: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val cardColor = Color.LightGray.copy(alpha = 0.4f)
    val (iconRes, tint) = remember(record.recordType, record.fuelType, isNightMode) { mapRecordToIcon(record, isNightMode) }
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.shadow(2.dp, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(tint, tint.copy(alpha = 0.9f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    record.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = if (expanded) Int.MAX_VALUE else 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) }
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(dateFormat.format(record.date), style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${record.odometer} km", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (!record.isReminder) {
                        Text(String.format(Locale.getDefault(), "%.2f €", record.cost ?: 0.0), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        if (record.recordType == RecordType.FUEL_UP) {
                            Text("${record.quantity ?: 0.0} lt", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        val badgeText = if (record.isCompleted) "Ολοκληρώθηκε" else "Εκκρεμεί"
                        Text(badgeText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(onClick = onEdit, label = { Text("Επεξεργασία") })
                    if (record.isReminder && !record.isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        SuggestionChip(onClick = onMarkCompleted, label = { Text("Ολοκλήρωση") })
                    }
                }
            }
        }
    }
}

// Dummy composable, replace with your actual implementation
@Composable
fun PremiumRoadBackground(listState: LazyListState, isNightMode: Boolean) {
    // Your road background implementation
}

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


private fun mapRecordToIcon(record: Record, isNightMode: Boolean): Pair<Int, Color> {
    val baseTint = when (record.recordType) {
        RecordType.FUEL_UP -> Color(0xFF0D47A1)
        RecordType.EXPENSE -> Color(0xFF4E342E)
        RecordType.REMINDER -> Color(0xFF00695C)
    }

    val iconRes = when (record.recordType) {
        RecordType.FUEL_UP -> R.drawable.ic_fuel_generic_filled
        RecordType.EXPENSE -> R.drawable.ic_expense_filled
        RecordType.REMINDER -> R.drawable.ic_reminder_filled
    }

    return iconRes to baseTint
}
