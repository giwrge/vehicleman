package com.vehicleman.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.*
import com.vehicleman.presentation.statistics.DetailedAnalysisViewModel
import kotlinx.coroutines.launch

// Metallic Blue Gradient Helper
val metallicBlueGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0D47A1), Color(0xFF2196F3)) // Dark Blue to Light Blue
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedAnalysisScreen(
    navController: NavController,
    isNightMode: Boolean,
    viewModel: DetailedAnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val blueAccent = Color(0xFF2196F3)
    val contentColor = if (isNightMode) Color.White else Color.Black
    val cardBackground = if (isNightMode) Color(0xFF1C1C1E) else Color.White
    val innerBoxBackground = if (isNightMode) Color(0xFF2C2C2E) else Color(0xFFF1F1F3)
    val borderColor = if (isNightMode) Color.White.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.3f)

    val showFab by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_statistic_background_night) else painterResource(id = R.mipmap.img_statistic_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(title.uppercase().removeAccents(), color = contentColor, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = contentColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = { coroutineScope.launch { scrollState.animateScrollToItem(0) } },
                        containerColor = if (isNightMode) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.94f),
                        contentColor = contentColor,
                        shape = CircleShape,
                        modifier = Modifier
                            .shadow(24.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.18f)),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 14.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                    }
                }
            }
        ) { paddingValues ->
            var totalDragAmount by remember { mutableFloatStateOf(0f) }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = contentColor, strokeWidth = 2.dp)
                }
            } else {
                state.statistics?.let { stats ->
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragStart = { totalDragAmount = 0f },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDragAmount += dragAmount
                                    },
                                    onDragEnd = {
                                        if (kotlin.math.abs(totalDragAmount) > 80) {
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            },
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "ΑΝΑΛΥΤΙΚΗ ΣΤΑΤΙΣΤΙΚΗ",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        item {
                            TimeFilterGrid(
                                currentFilter = state.timeFilter,
                                onFilterSelected = { viewModel.onTimeFilterSelected(it) },
                                isNightMode = isNightMode,
                                borderColor = borderColor
                            )
                        }

                        item { SummaryGrid(stats.summary, contentColor, cardBackground, borderColor) }

                        item { ProfessionalSectionDivider("ΑΝΑΛΥΣΗ ΚΑΥΣΙΜΟΥ", contentColor) }
                        items(stats.fuelStats) { fuelGroup ->
                            FuelAnalysisCard(fuelGroup, contentColor, cardBackground, innerBoxBackground, borderColor)
                        }

                        item {
                            PremiumStatisticsCard(title = "ΚΑΤΑΝΟΜΗ ΑΝΑ ΤΥΠΟ ΚΑΥΣΙΜΟΥ", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    RawFuelPieChart(stats.fuelDistributionRaw, contentColor)
                                }
                            }
                        }

                        item {
                            PremiumStatisticsCard(title = "ΚΟΣΤΟΣ ΑΝΑ ΛΙΤΡΟ & ΤΙΜΗ", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    CombinedFuelChart(stats.charts.costPerLiterPerFillUp, stats.charts.fuelPriceTrend, contentColor, Color(0xFF4CAF50))
                                }
                            }
                        }

                        item { ProfessionalSectionDivider("ΣΥΝΤΗΡΗΣΗ & ΧΡΗΣΗ", contentColor) }
                        item { MaintenanceCard(stats.expenseStats, contentColor, cardBackground, innerBoxBackground, borderColor) }
                        item { UsageCard(stats.usageStats, contentColor, cardBackground, innerBoxBackground, borderColor) }

                        item { ProfessionalSectionDivider("ΟΙΚΟΝΟΜΙΚΑ ΣΤΟΙΧΕΙΑ", contentColor) }
                        item {
                            PremiumStatisticsCard(title = "ΚΑΤΑΝΟΜΗ ΕΞΟΔΩΝ (%)", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    ExpensePieChart(stats.expenseStats.categoryPercentages, contentColor)
                                }
                            }
                        }
                        item {
                            PremiumStatisticsCard(title = "ΚΟΣΤΟΣ ΑΝΑ ΚΑΤΗΓΟΡΙΑ (€)", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    HorizontalBarChart(stats.expenseStats.categoryBreakdown, contentColor, blueAccent)
                                }
                            }
                        }

                        item { ProfessionalSectionDivider("ΑΠΟΔΟΣΗ & ΤΑΣΕΙΣ", contentColor) }
                        item {
                            PremiumStatisticsCard(title = "ΤΑΣΗ ΚΑΤΑΝΑΛΩΣΗΣ (L/100KM)", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    EnhancedLineChart(stats.charts.consumptionTrend, Color.Red, contentColor)
                                }
                            }
                        }
                        item {
                            PremiumStatisticsCard(title = "ΜΗΝΙΑΙΑ ΕΞΟΔΑ", contentColor = contentColor, containerColor = cardBackground, borderColor = borderColor) {
                                Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
                                    StackedMonthlyChart(stats.charts.monthlyExpenses, contentColor, blueAccent)
                                }
                            }
                        }

                        if (stats.insights.isNotEmpty()) {
                            item { ProfessionalSectionDivider("INSIGHTS", contentColor) }
                            item { InsightsSection(stats.insights, contentColor, cardBackground, borderColor) }
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

// Extension to remove accents for Greek uppercase text
fun String.removeAccents(): String {
    val accents = mapOf(
        'Ά' to 'Α', 'Έ' to 'Ε', 'Ή' to 'Η', 'Ί' to 'Ι', 'Ό' to 'Ο', 'Ύ' to 'Υ', 'Ώ' to 'Ω',
        'Ϊ' to 'Ι', 'Ϋ' to 'Υ'
    )
    return this.map { accents.getOrDefault(it, it) }.joinToString("")
}

@Composable
fun ProfessionalSectionDivider(text: String, contentColor: Color) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
        Text(
            text = text.uppercase().removeAccents(),
            color = contentColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = contentColor.copy(alpha = 0.15f))
    }
}

@Composable
fun PremiumStatisticsCard(
    title: String,
    contentColor: Color,
    containerColor: Color,
    borderColor: Color,
    summaryValue: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(0.6.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title.uppercase().removeAccents(),
                fontSize = 14.sp,
                color = contentColor.copy(alpha = 0.6f),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            if (summaryValue != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summaryValue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(brush = metallicBlueGradient)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun PremiumTimePill(
    text: String,
    selected: Boolean,
    isNightMode: Boolean,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val elevation by animateDpAsState(targetValue = if (selected) 10.dp else 2.dp, label = "elevation")
    val textColor = if (selected) Color.White else (if (isNightMode) Color.White else Color(0xFF111111))
    val currentBorderColor = if (selected) Color.Transparent else borderColor

    val backgroundBrush = if (selected) metallicBlueGradient else {
        if (isNightMode) Brush.verticalGradient(listOf(Color(0xFF3A3A3C), Color(0xFF2C2C2E)))
        else Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF2F2F2)))
    }

    Box(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(100))
            .background(backgroundBrush, RoundedCornerShape(100))
            .border(0.6.dp, currentBorderColor, RoundedCornerShape(100))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(12.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
            )
        }
        Text(
            text = text.uppercase().removeAccents(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
    }
}

// --- Specialized Charts ---

@Composable
fun EnhancedLineChart(points: List<DataPoint>, color: Color, contentColor: Color) {
    if (points.isEmpty()) return
    val maxVal = points.maxOf { it.value }.toFloat()
    val minVal = points.minOf { it.value }.toFloat()
    val range = (maxVal - minVal).coerceAtLeast(0.1f)
    val avgVal = points.map { it.value }.average().toFloat()

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            val spacing = size.width / (points.size - 1).coerceAtLeast(1)
            
            // Draw grid lines and labels
            val yLines = 3
            for (i in 0..yLines) {
                val yPos = size.height - (i.toFloat() / yLines) * size.height
                drawLine(contentColor.copy(alpha = 0.05f), Offset(0f, yPos), Offset(size.width, yPos), strokeWidth = 1.dp.toPx())
            }

            // Draw path
            val path = Path()
            points.forEachIndexed { i, dp ->
                val x = i * spacing
                val y = size.height - ((dp.value.toFloat() - minVal) / range) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            
            // Draw points
            points.forEachIndexed { i, dp ->
                val x = i * spacing
                val y = size.height - ((dp.value.toFloat() - minVal) / range) * size.height
                drawCircle(color, radius = 5.dp.toPx(), center = Offset(x, y))
                drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            ChartLabel("MIN: ${String.format("%.1f", minVal)}", contentColor)
            ChartLabel("AVG: ${String.format("%.1f", avgVal)}", contentColor)
            ChartLabel("MAX: ${String.format("%.1f", maxVal)}", contentColor)
        }
    }
}

@Composable
fun ChartLabel(text: String, color: Color) {
    Text(text, color = color.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun RawFuelPieChart(data: Map<String, Double>, contentColor: Color) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFF9C27B0))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(100.dp)) {
            var startAngle = -90f
            sortedData.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second.toFloat() / 100f) * 360f
                drawArc(colors[index % colors.size], startAngle, sweepAngle, true)
                startAngle += sweepAngle
            }
            drawCircle(Color.Transparent, radius = size.width / 2.8f, blendMode = BlendMode.Clear)
        }
        Spacer(Modifier.width(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sortedData.forEachIndexed { index, pair ->
                LegendItem("${pair.first}: ${pair.second.toInt()}%", colors[index % colors.size], contentColor)
            }
        }
    }
}

@Composable
fun ExpensePieChart(data: Map<RecordExpenseCategory, Double>, contentColor: Color) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val colors = listOf(Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFFF44336), Color(0xFF00BCD4), Color(0xFF9C27B0))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(100.dp)) {
            var startAngle = -90f
            sortedData.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second.toFloat() / 100f) * 360f
                drawArc(colors[index % colors.size], startAngle, sweepAngle, true)
                startAngle += sweepAngle
            }
        }
        Spacer(Modifier.width(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sortedData.take(6).forEachIndexed { index, pair ->
                LegendItem("${pair.first.name.uppercase().removeAccents()}: ${pair.second.toInt()}%", colors[index % colors.size], contentColor)
            }
        }
    }
}

@Composable
fun CombinedFuelChart(costPerL: List<DataPoint>, priceTrend: List<DataPoint>, contentColor: Color, accentColor: Color) {
    if (costPerL.isEmpty()) return
    val maxCost = costPerL.maxOf { it.value }.toFloat()
    val maxPrice = priceTrend.maxOfOrNull { it.value }?.toFloat() ?: 1f
    val ceiling = kotlin.math.max(maxCost, maxPrice) * 1.1f

    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val spacing = size.width / (costPerL.size.coerceAtLeast(1))
        val barWidth = spacing * 0.4f

        costPerL.forEachIndexed { i, dp ->
            val x = i * spacing + (spacing / 2)
            val barHeight = (dp.value.toFloat() / ceiling) * size.height
            drawRect(accentColor.copy(alpha = 0.3f), Offset(x - barWidth/2, size.height - barHeight), Size(barWidth, barHeight))
        }

        if (priceTrend.size > 1) {
            val path = Path()
            priceTrend.forEachIndexed { i, dp ->
                val x = i * spacing + (spacing / 2)
                val y = size.height - (dp.value.toFloat() / ceiling) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(contentColor, radius = 3f, center = Offset(x, y))
            }
            drawPath(path, contentColor.copy(alpha = 0.8f), style = Stroke(width = 2f, cap = StrokeCap.Round))
        }
    }
}

@Composable
fun StackedMonthlyChart(monthlyData: List<MonthlyExpensePoint>, contentColor: Color, accentColor: Color) {
    if (monthlyData.isEmpty()) return
    val maxTotal = monthlyData.maxOf { it.total }.toFloat()
    val ceiling = maxTotal * 1.1f

    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val spacing = size.width / (monthlyData.size.coerceAtLeast(1))
        val barWidth = spacing * 0.5f

        monthlyData.forEachIndexed { i, data ->
            val x = i * spacing + (spacing / 2)
            val fH = (data.fuelCost.toFloat() / ceiling) * size.height
            val oH = (data.otherExpenses.toFloat() / ceiling) * size.height

            drawRect(accentColor.copy(alpha = 0.6f), Offset(x - barWidth/2, size.height - fH), Size(barWidth, fH))
            drawRect(contentColor.copy(alpha = 0.1f), Offset(x - barWidth/2, size.height - fH - oH), Size(barWidth, oH))
        }
    }
    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
        LegendItem("ΚΑΥΣΙΜΑ", accentColor, contentColor)
        Spacer(Modifier.width(24.dp))
        LegendItem("ΛΟΙΠΑ", contentColor.copy(alpha = 0.3f), contentColor)
    }
}

@Composable
fun LegendItem(label: String, color: Color, contentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, RoundedCornerShape(3.dp)))
        Text(label.uppercase().removeAccents(), color = contentColor.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun HorizontalBarChart(data: Map<RecordExpenseCategory, Double>, contentColor: Color, accentColor: Color) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val maxVal = sortedData.maxOfOrNull { it.second } ?: 1.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sortedData.forEach { (cat, value) ->
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text(cat.name.uppercase().removeAccents(), color = contentColor.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text("${value.toInt()}€", color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(6.dp).background(contentColor.copy(alpha = 0.05f), RoundedCornerShape(3.dp))) {
                    Box(Modifier.fillMaxWidth((value / maxVal).toFloat().coerceIn(0.01f, 1f)).fillMaxHeight().background(accentColor, RoundedCornerShape(3.dp)))
                }
            }
        }
    }
}

// --- Helper Layout Components ---

@Composable
fun SummaryGrid(summary: SummaryStats, contentColor: Color, cardBackground: Color, borderColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryPremiumCard("ΣΥΝΟΛΙΚΟ ΚΟΣΤΟΣ", String.format("%.2f €", summary.totalCost), contentColor, cardBackground, borderColor, Modifier.weight(1f))
            SummaryPremiumCard("ΚΟΣΤΟΣ / ΧΛΜ", String.format("%.3f €/KM", summary.costPerKm), contentColor, cardBackground, borderColor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryPremiumCard("ΜΕΣΗ ΚΑΤΑΝΑΛΩΣΗ", String.format("%.1f L/100", summary.averageConsumption), contentColor, cardBackground, borderColor, Modifier.weight(1f))
            SummaryPremiumCard("ΔΙΑΝΥΘΕΝΤΑ ΧΛΜ", "${summary.totalDistance} KM", contentColor, cardBackground, borderColor, Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryPremiumCard(title: String, value: String, contentColor: Color, containerColor: Color, borderColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(24.dp), border = BorderStroke(0.6.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title.uppercase().removeAccents(), modifier = Modifier, color = contentColor.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, style = TextStyle(brush = metallicBlueGradient))
        }
    }
}

@Composable
fun FuelAnalysisCard(group: FuelTypeGroupStats, contentColor: Color, containerColor: Color, innerBoxBackground: Color, borderColor: Color) {
    PremiumStatisticsCard(
        title = "ΟΜΑΔΑ: ${group.fuelType.uppercase().removeAccents()}",
        summaryValue = String.format("%.2f € / %.1f L", group.costPer100Km, group.totalLiters),
        contentColor = contentColor,
        containerColor = containerColor,
        borderColor = borderColor
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FuelInfoItem("ΜΕΣΗ ΚΑΤΑΝ.", String.format("%.1f L", group.averageConsumption), contentColor)
                    FuelInfoItem("ΜΕΣΗ ΤΙΜΗ", String.format("%.3f €", group.averagePricePerLiter), contentColor)
                    FuelInfoItem("ΚΟΣΤΟΣ/100KM", String.format("%.2f €", group.costPer100Km), contentColor)
                }
                Column {
                    LinearProgressIndicator(
                        progress = { (group.percentageOfTotalFuel / 100).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = contentColor.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ΣΥΝΟΛΟ: ${group.totalLiters} LITERS", color = contentColor.copy(alpha = 0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun MaintenanceCard(stats: ExpenseAnalysis, contentColor: Color, containerColor: Color, innerBoxBackground: Color, borderColor: Color) {
    PremiumStatisticsCard(
        title = "ΔΑΠΑΝΕΣ & ΣΥΝΤΗΡΗΣΗ",
        summaryValue = "${stats.lastServiceDaysAgo ?: "?"} ΗΜΕΡΕΣ ΠΡΙΝ",
        contentColor = contentColor,
        containerColor = containerColor,
        borderColor = borderColor
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MaintenanceInfoRow("ΤΕΛΕΥΤΑΙΟ SERVICE", "${stats.lastServiceKmAgo ?: "?"} ΧΛΜ", contentColor)
                MaintenanceInfoRow("ΕΠΟΜΕΝΟ SERVICE (ΠΡΟΒΛΕΨΗ)", "${stats.nextServicePredictionMonths ?: "N/A"} ΜΗΝΕΣ", contentColor)
            }
        }
    }
}

@Composable
fun MaintenanceInfoRow(label: String, value: String, contentColor: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label.uppercase().removeAccents(), color = contentColor.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Text(value, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun UsageCard(usage: UsageStats, contentColor: Color, containerColor: Color, innerBoxBackground: Color, borderColor: Color) {
    val predictionColor = Color(0xFFFFC107)
    
    PremiumStatisticsCard(
        title = "ΧΡΗΣΗ & ΠΡΟΒΛΕΨΕΙΣ",
        summaryValue = "${usage.predictedYearlyDistance} KM / ΕΤΟΣ",
        contentColor = contentColor,
        containerColor = containerColor,
        borderColor = borderColor
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(innerBoxBackground, RoundedCornerShape(22.dp)).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FuelInfoItem("ΗΜΕΡΗΣΙΑ", "${String.format("%.1f", usage.averageDailyDistance)} KM", contentColor, valueColor = predictionColor)
                    FuelInfoItem("ΕΒΔΟΜΑΔΙΑΙΑ", "${String.format("%.1f", usage.averageWeeklyDistance)} KM", contentColor, valueColor = predictionColor)
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ΠΡΟΒΛΕΨΗ ΚΟΣΤΟΥΣ & ΧΛΜ", color = contentColor.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("${usage.predictedYearlyDistance} KM", color = predictionColor, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Text("${String.format("%.0f", usage.predictedYearlyCost)} €", color = predictionColor, fontWeight = FontWeight.Black, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun InsightsSection(insights: List<String>, contentColor: Color, cardBackground: Color, borderColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        insights.forEach { insight ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(cardBackground).border(0.6.dp, borderColor, RoundedCornerShape(24.dp)).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(insight, color = contentColor, fontSize = 13.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun FuelInfoItem(label: String, value: String, contentColor: Color, valueColor: Color? = null) {
    Column {
        Text(label.uppercase().removeAccents(), color = contentColor.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor ?: contentColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun TimeFilterGrid(currentFilter: TimeFilter, onFilterSelected: (TimeFilter) -> Unit, isNightMode: Boolean, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(TimeFilter.LAST_WEEK to "WEEK", TimeFilter.LAST_MONTH to "MONTH", TimeFilter.SIX_MONTHS to "6 MONTH", TimeFilter.YEAR to "YEAR").forEach { (filter, label) ->
                PremiumTimePill(label, currentFilter == filter, isNightMode, borderColor, Modifier.weight(if (filter == TimeFilter.SIX_MONTHS) 1.2f else 1f)) { onFilterSelected(filter) }
            }
        }
        Spacer(Modifier.height(10.dp))
        PremiumTimePill("SUMMARY", currentFilter == TimeFilter.SUMMARY, isNightMode, borderColor, Modifier.fillMaxWidth()) { onFilterSelected(TimeFilter.SUMMARY) }
    }
}
