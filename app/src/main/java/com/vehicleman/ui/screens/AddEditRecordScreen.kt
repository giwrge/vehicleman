package com.vehicleman.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.presentation.addeditrecord.AddEditRecordEvent
import com.vehicleman.presentation.addeditrecord.AddEditRecordViewModel
import com.vehicleman.presentation.record.mapCategoryToIcon
import com.vehicleman.domain.use_case.record_ai.SuggestionItem
import com.vehicleman.domain.use_case.record_ai.SuggestionSource
import com.vehicleman.ui.screens.components.GlassBox
import com.vehicleman.ui.screens.components.GlassTextField
import com.vehicleman.ui.screens.components.GlassTextFieldWithSelection
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    navController: NavController,
    isNightMode: Boolean,
    viewModel: AddEditRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val contentColor = if (isNightMode) Color.White else Color(0xFF1A1A1A)
    
    // Glass styling consistent across screens
    val glassColor = if (isNightMode) Color(0xFF707070).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.92f)
    val glassBorderColor = if (isNightMode) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.12f)

    // Error Handling
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.onEvent(AddEditRecordEvent.ErrorShown)
        }
    }

    // Navigation Handling
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(AddEditRecordEvent.NavigateBackConsumed)
        }
    }

    val scrollState = rememberScrollState()

    // Date Picker Logic
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            viewModel.onEvent(AddEditRecordEvent.DateChanged(Date(it)))
            showDatePicker = false
        }
    }

    // Translate Title Dialog
    if (state.showTranslateTitleDialog) {
        var dontAskAgain by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = false, dontAskAgain = false)) },
            title = { Text("Μετάφραση Τίτλου") },
            text = { 
                Column {
                    Text("Ο τίτλος φαίνεται να είναι γραμμένος σε Grenglish. Θέλετε να τον μεταφράσουμε στα Ελληνικά;") 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dontAskAgain, onCheckedChange = { dontAskAgain = it })
                        Text("Να μην ερωτηθώ ξανά")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = true, dontAskAgain = dontAskAgain)) }) {
                    Text("Ναι")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(AddEditRecordEvent.TranslateTitleConfirmation(translate = false, dontAskAgain = dontAskAgain)) }) {
                    Text("Όχι")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_home_background_night) else painterResource(id = R.mipmap.img_home_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isNightMode) 70.dp else 24.dp) 
        )

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (state.isNew) "Νέα Εγγραφή" else "Επεξεργασία",
                            color = contentColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(48.dp)) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_backarrow),
                                contentDescription = "Back",
                                modifier = Modifier.fillMaxSize() 
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(AddEditRecordEvent.Save) }, modifier = Modifier.size(48.dp)) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_save),
                                contentDescription = "Save",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = contentColor)
                    }
                } else {
                    // ROW 1: PRIMARY HEADER (Title and Mini Calendar Widget)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassBox(
                            modifier = Modifier.weight(1.8f).fillMaxHeight(),
                            label = "Τίτλος",
                            icon = R.drawable.ic_service,
                            isNightMode = isNightMode,
                            glassColor = glassColor,
                            glassBorderColor = glassBorderColor
                        ) {
                            GlassTextFieldWithSelection(
                                value = state.titleFieldValue,
                                onValueChange = { tfv -> viewModel.onEvent(AddEditRecordEvent.TitleFieldValueChanged(tfv)) },
                                placeholder = "π.χ. Service ή Βενζίνη 50€",
                                isNightMode = isNightMode,
                                onFocusChanged = { isFocused ->
                                    if (!isFocused) viewModel.onEvent(AddEditRecordEvent.TitleFocusLost)
                                }
                            )
                        }

                        GlassBox(
                            modifier = Modifier.weight(1.2f).fillMaxHeight(),
                            onClick = { showDatePicker = true },
                            isNightMode = isNightMode,
                            glassColor = glassColor,
                            glassBorderColor = glassBorderColor,
                            padding = 10.dp
                        ) {
                            MiniCalendarBox(
                                date = state.date,
                                isNightMode = isNightMode,
                                onClick = { showDatePicker = true }
                            )
                        }
                    }

                    // ROW 2: AI Suggestions (Moved under title)
                    if (state.suggestions.isNotEmpty()) {
                        SuggestionsGrid(
                            suggestions = state.suggestions,
                            onSuggestionClick = { viewModel.onEvent(AddEditRecordEvent.SuggestionClicked(it)) },
                            isNightMode = isNightMode,
                            glassColor = glassColor,
                            glassBorderColor = glassBorderColor
                        )
                    }
                    if (state.recordType != RecordType.REMINDER || !state.isFutureDate) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (state.recordType != RecordType.REMINDER) {
                                GlassBox(
                                    modifier = Modifier.weight(1f),
                                    label = "Χιλιόμετρα",
                                    icon = R.drawable.ic_service,
                                    isNightMode = isNightMode,
                                    glassColor = glassColor,
                                    glassBorderColor = glassBorderColor
                                ) {
                                    GlassTextField(
                                        value = state.odometer,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.OdometerChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0",
                                        isNightMode = isNightMode
                                    )
                                }
                            }
                            
                            if (!state.isFutureDate) {
                                GlassBox(
                                    modifier = Modifier.weight(1f),
                                    label = "Κόστος",
                                    icon = R.drawable.ic_expense_filled,
                                    isNightMode = isNightMode,
                                    glassColor = glassColor,
                                    glassBorderColor = glassBorderColor
                                ) {
                                    GlassTextField(
                                        value = state.cost,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.CostChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0.00 €",
                                        isNightMode = isNightMode
                                    )
                                }
                            }
                        }
                    }

                    // ROW 4: FUEL DATA (Enhanced with Selection Chips)
                    if (state.recordType == RecordType.FUEL_UP) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GlassBox(modifier = Modifier.weight(1f), label = "Λίτρα", icon = R.drawable.ic_fuel, isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    GlassTextField(
                                        value = state.quantity,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.QuantityChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0.0",
                                        isNightMode = isNightMode
                                    )
                                }
                                GlassBox(modifier = Modifier.weight(1f), label = "€/lt", isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    GlassTextField(
                                        value = state.pricePerUnit,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.PricePerUnitChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0.000",
                                        isNightMode = isNightMode
                                    )
                                }
                            }

                            // Fuel Type Selection Chips from Vehicle List
                            if (state.vehicleFuelTypes.isNotEmpty()) {
                                GlassBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    label = "Τύπος Καυσίμου Οχήματος",
                                    isNightMode = isNightMode,
                                    glassColor = glassColor,
                                    glassBorderColor = glassBorderColor
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        state.vehicleFuelTypes.forEach { fuelType ->
                                            FuelChip(
                                                fuelType = fuelType,
                                                isSelected = state.fuelTypeText == fuelType,
                                                onClick = { viewModel.onEvent(AddEditRecordEvent.FuelTypeChanged(fuelType)) },
                                                isNightMode = isNightMode
                                            )
                                        }

                                        // Full Tank Toggle Chip
                                        FuelChip(
                                            fuelType = "full_tank",
                                            displayName = "Γέμισμα Ρεζερβουάρ",
                                            isSelected = state.isFullTank,
                                            onClick = { viewModel.onEvent(AddEditRecordEvent.ToggleFullTank) },
                                            isNightMode = isNightMode,
                                            activeColor = Color(0xFF2196F3)
                                        )
                                    }
                                }
                            } else {
                                // Fallback to manual entry if no fuel types defined for vehicle
                                GlassBox(modifier = Modifier.fillMaxWidth(), label = "Τύπος (Μη ορισμένο)", isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    GlassTextField(
                                        value = state.fuelTypeText,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.FuelTypeChanged(it)) },
                                        placeholder = "95",
                                        isNightMode = isNightMode
                                    )
                                }
                            }
                        }
                    }

                    // ROW 5: REMINDER GROUP (2x2 Logic)
                    if (state.recordType == RecordType.REMINDER) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GlassBox(modifier = Modifier.weight(1f), label = "Ημ/νία Υπενθ.", icon = R.drawable.ic_bell, isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                                        Text(
                                            text = if (state.reminderDateText.isEmpty()) "dd/mm/yyyy" else state.reminderDateText,
                                            color = contentColor.copy(alpha = if (state.reminderDateText.isEmpty()) 0.4f else 1f),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                GlassBox(modifier = Modifier.weight(1f), label = "Km Υπενθ.", isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    GlassTextField(
                                        value = state.reminderOdometer,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.ReminderOdometerChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0",
                                        isNightMode = isNightMode
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GlassBox(
                                    modifier = Modifier.weight(1f),
                                    label = "Ολοκληρώθηκε",
                                    onClick = { viewModel.onEvent(AddEditRecordEvent.ToggleCompleted) },
                                    isNightMode = isNightMode,
                                    glassColor = glassColor,
                                    glassBorderColor = glassBorderColor
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (state.isCompleted) Color(0xFF4CAF50) else contentColor.copy(alpha = 0.2f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                GlassBox(modifier = Modifier.weight(1f), label = "Κόστος Υπενθ.", isNightMode = isNightMode, glassColor = glassColor, glassBorderColor = glassBorderColor) {
                                    GlassTextField(
                                        value = state.costReminder,
                                        onValueChange = { viewModel.onEvent(AddEditRecordEvent.CostReminderChanged(it)) },
                                        keyboardType = KeyboardType.Number,
                                        placeholder = "0.00 €",
                                        isNightMode = isNightMode
                                    )
                                }
                            }
                        }
                    }

                    // ROW 6: ADDITIONAL INFO (Full Width Description Widget)
                    GlassBox(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Περιγραφή",
                        icon = R.mipmap.ic_wrench_edit,
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor
                    ) {
                        GlassTextField(
                            value = state.description,
                            onValueChange = { viewModel.onEvent(AddEditRecordEvent.DescriptionChanged(it)) },
                            singleLine = false,
                            modifier = Modifier.heightIn(min = 100.dp),
                            placeholder = "Προαιρετική περιγραφή...",
                            isNightMode = isNightMode
                        )
                    }

                    // ROW 7: SYSTEM SMART WIDGET (Category)
                    state.category?.let { category ->
                        CategoryGlassPreview(category, isNightMode, glassColor, glassBorderColor)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Άκυρο")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun FuelChip(
    fuelType: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isNightMode: Boolean,
    displayName: String? = null, // ✅ Added
    activeColor: Color = Color(0xFF4CAF50) // ✅ Added
) {
    val finalDisplayName = displayName ?: when (fuelType) {
        "unleaded_95" -> "95"
        "unleaded_98" -> "98"
        "unleaded_100" -> "100"
        "diesel" -> "Diesel"
        "lpg" -> "LPG"
        "electric" -> "Ρεύμα"
        else -> fuelType
    }
    val contentColor = if (isNightMode) Color.White else Color.Black
    val backgroundColor = if (isSelected) activeColor else contentColor.copy(alpha = 0.1f)

    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = finalDisplayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else contentColor
            )
        }
    }
}

@Composable
private fun SuggestionsGrid(
    suggestions: List<SuggestionItem>,
    onSuggestionClick: (SuggestionItem) -> Unit,
    isNightMode: Boolean,
    glassColor: Color,
    glassBorderColor: Color
) {
    val chunks = suggestions.take(4).chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunks.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    GlassBox(
                        modifier = Modifier.weight(1f),
                        onClick = { onSuggestionClick(item) },
                        isNightMode = isNightMode,
                        glassColor = glassColor,
                        glassBorderColor = glassBorderColor,
                        padding = 12.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.text,
                                color = if (isNightMode) Color.White else Color.Black,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            SuggestionPill(source = item.source)
                        }
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SuggestionPill(source: SuggestionSource) {
    val (text, color) = when (source) {
        SuggestionSource.RECENT_RECORD -> "REC" to Color(0xFF64B5F6)
        SuggestionSource.DOMAIN_KEYWORD -> "AI" to Color(0xFF81C784)
    }

    Surface(
        color = color.copy(alpha = 0.35f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 7.sp
        )
    }
}

@Composable
private fun CategoryGlassPreview(category: RecordCategory, isNightMode: Boolean, glassColor: Color, glassBorderColor: Color) {
    val iconRes = mapCategoryToIcon(category)
    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        label = "Αυτόματη Κατηγορία",
        isNightMode = isNightMode,
        glassColor = glassColor,
        glassBorderColor = glassBorderColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = category::class.simpleName ?: "Άγνωστη",
                color = if (isNightMode) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MiniCalendarBox(
    date: Date,
    isNightMode: Boolean,
    onClick: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = date }
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val monthYear = SimpleDateFormat("MMM yyyy", Locale("el")).format(date)
    
    val days = remember(date) {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val month = cal.get(Calendar.MONTH)
        val list = mutableListOf<Int?>()
        // Determine first day of week (Monday as start)
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        for (i in 0 until firstDayOfWeek) list.add(null)
        while (cal.get(Calendar.MONTH) == month) {
            list.add(cal.get(Calendar.DAY_OF_MONTH))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }

    val contentColor = if (isNightMode) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = monthYear.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = contentColor.copy(alpha = 0.85f),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        val rows = days.chunked(7)
        rows.take(5).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = if (day == currentDay) Color(0xFF4CAF50) else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                fontSize = 8.sp,
                                color = if (day == currentDay) Color.White else contentColor,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 8.sp
                            )
                        }
                    }
                }
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
