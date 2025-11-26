package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import com.vehicleman.presentation.record.RecordEvent
import com.vehicleman.presentation.record.RecordViewModel
import com.vehicleman.presentation.record.mapCategoryToIcon
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

    val categorizer = remember { RecordCategorizerUseCase() }

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
            val firstVisibleDate = if (firstVisibleRecord.isReminder) firstVisibleRecord.reminderDate ?: firstVisibleRecord.date else firstVisibleRecord.date
            (firstVisibleDate != null && sticky.reminderDate != null) && firstVisibleDate.before(sticky.reminderDate)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PremiumRoadBackground(listState = listState, isNightMode = isNightMode)

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
                FloatingActionButton(
                    onClick = { state.selectedVehicleId?.let { onNavigateToAddEditRecord(it, "new") } },
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_add_record),
                        contentDescription = "Νέα εγγραφή",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = if (!showStickyReminder) 8.dp else 0.dp, bottom = 80.dp) // Space for FAB
                ) {
                    items(state.timelineItems, key = { it.id }) { record ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                when (value) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        scope.launch {
                                            recentlyDeleted = record
                                            viewModel.deleteRecord(record)
                                            val result = snackbarHostState.showSnackbar("Η εγγραφή διαγράφηκε", "ΑΝΑΙΡΕΣΗ")
                                            if (result == SnackbarResult.ActionPerformed) {
                                                recentlyDeleted?.let { viewModel.saveRecord(it) }
                                            }
                                        }
                                        true // Confirm the dismiss
                                    }
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        onNavigateToAddEditRecord(record.vehicleId, record.id)
                                        false // Do not dismiss the item, just trigger navigation and snap back
                                    }
                                    SwipeToDismissBoxValue.Settled -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { SwipeBackground(dismissState) },
                            enableDismissFromEndToStart = true,
                            enableDismissFromStartToEnd = true // Enable swipe right
                        ) {
                            TimelineRow(
                                record = record,
                                isNightMode = isNightMode,
                                onMarkCompleted = { viewModel.onEvent(RecordEvent.MarkReminderCompleted(record.id)) },
                                categorizer = categorizer
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
                Icon(
                    painter = painterResource(R.mipmap.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToStatistics) {
                Icon(
                    painter = painterResource(R.mipmap.ic_statistic_bar),
                    contentDescription = "Statistics",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onNavigateToSignUp) {
                Icon(
                    painter = painterResource(R.mipmap.ic_sing_up),
                    contentDescription = "Sign Up",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onNavigateToProMode) {
                Icon(
                    painter = painterResource(R.mipmap.ic_promode_vip),
                    contentDescription = "Pro Mode",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onNavigateToPreferences) {
                Icon(
                    painter = painterResource(R.mipmap.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
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

    val bgColor = if (isNightMode)
        Color(0xFFFFC94D).copy(alpha = 0.90f)
    else
        Color(0xFFFFF2B0).copy(alpha = 0.92f)

    val shadowColor = if (isNightMode)
        Color(0xFF000000).copy(alpha = 0.45f)
    else
        Color(0xFF333333).copy(alpha = 0.25f)

    Card(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .shadow(
                elevation = 10.dp,
                spotColor = shadowColor,
                ambientColor = shadowColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bell Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Reminder",
                tint = Color.Unspecified,
                modifier = Modifier.size(42.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                record.reminderDate?.let {
                    Text(
                        "Στις: ${dateFormat.format(it)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Days left bubble
            record.reminderDate?.let {
                val daysLeft =
                    ((it.time - System.currentTimeMillis()) / (1000L * 60 * 60 * 24)).coerceAtLeast(0)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (daysLeft <= 3) Color(0xFFE53935)
                            else if (daysLeft <= 10) Color(0xFFFDD835)
                            else Color(0xFF43A047)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "$daysLeft d",
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
                    )
                }
            }
        }
    }
}


@Composable
private fun TimelineRow(
    record: Record,
    isNightMode: Boolean,
    onMarkCompleted: () -> Unit,
    categorizer: RecordCategorizerUseCase
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val category = remember(record.title, record.description, record.isReminder) {
        categorizer(record.title, record.isReminder, record.description)
    }


    val iconRes = remember(category) {
        mapCategoryToIcon(category)
    }

    // Elegant transparent card
    val alphaBg = if (isNightMode) 0.20f else 0.10f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alphaBg)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Icon with overlay
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(34.dp)
                    )

                    if (record.isReminder) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Title & Info
                Column(Modifier.weight(1f)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            record.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Auto Tag
                        if (record.description == null && record.isReminder) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isNightMode) Color(0xFF3949AB)
                                        else Color(0xFF5C6BC0)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "AUTO",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Date + KM
                    Row {
                        val displayDate =
                            record.reminderDate ?: record.date

                        Text(
                            dateFormat.format(displayDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.width(10.dp))

                        if (!record.isReminder) {
                            Text(
                                "${record.odometer} km",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Right Info Column
                Column(horizontalAlignment = Alignment.End) {
                    if (!record.isReminder) {
                        val cost = record.cost ?: 0.0
                        Text(
                            String.format("%.2f €", cost),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (record.recordType.name == "FUEL_UP") {
                            record.quantity?.let {
                                Text("$it lt", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                    } else {
                        val txt = if (record.isCompleted) "✔ Ολοκληρώθηκε"
                        else "⏳ Εκκρεμεί"

                        Text(
                            txt,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Description + Actions
            record.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 56.dp)
                )
            }

            // Action for reminders
            if (record.isReminder && !record.isCompleted) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    SuggestionChip(label = { Text("Ολοκλήρωση") }, onClick = onMarkCompleted)
                }
            }
        }
    }
}

@Composable
private fun PremiumRoadBackground(listState: LazyListState, isNightMode: Boolean) {
    val imageRes = if (isNightMode) R.drawable.road_night else R.drawable.road_light
    val painter = painterResource(id = imageRes)

    val infiniteTransition = rememberInfiniteTransition(label = "road_background_anim")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing)
        ),
        label = "road_background_offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val painterIntrinsicSize = painter.intrinsicSize
        if (painterIntrinsicSize == Size.Unspecified || painterIntrinsicSize.height == 0f) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        val scaledImageHeight = (canvasWidth / painterIntrinsicSize.width) * painterIntrinsicSize.height
        val scaledImageSize = Size(canvasWidth, scaledImageHeight)

        val scrollBasedOffset = listState.firstVisibleItemScrollOffset / 3f
        val constantOffset = animationOffset * scaledImageHeight
        val totalOffset = scrollBasedOffset + constantOffset

        var yPos = -(totalOffset % scaledImageHeight)

        while (yPos < canvasHeight) {
            translate(left = 0f, top = yPos) {
                with(painter) {
                    draw(size = scaledImageSize)
                }
            }
            yPos += scaledImageHeight
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.targetValue
    val progress = dismissState.progress
    val visibilityThreshold = 0.15f

    // Alpha is 1f (fully visible) if the threshold is passed in either swipe direction.
    val alpha = if ((direction == SwipeToDismissBoxValue.StartToEnd || direction == SwipeToDismissBoxValue.EndToStart) && progress > visibilityThreshold) {
        1f
    } else {
        0f
    }

    val (alignment, iconResId) = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd to R.mipmap.ic_delete
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart to R.mipmap.ic_wrench_edit
        else -> Alignment.Center to null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        iconResId?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null, // Decorative
                modifier = Modifier
                    .size(38.dp) // Increased size for both icons
                    .alpha(alpha.coerceIn(0f, 1f))
            )
        }
    }
}
