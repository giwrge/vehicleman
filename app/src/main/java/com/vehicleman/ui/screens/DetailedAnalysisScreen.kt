package com.vehicleman.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.model.*
import com.vehicleman.presentation.statistics.DetailedAnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedAnalysisScreen(
    navController: NavController,
    isNightMode: Boolean,
    viewModel: DetailedAnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    
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
                    title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.4f))
                )
            }
        ) { paddingValues ->
            var totalDragAmount by remember { mutableStateOf(0f) }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                state.statistics?.let { stats ->
                    LazyColumn(
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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // 1. Time Filters
                        item {
                            TimeFilterSection(
                                currentFilter = state.timeFilter,
                                onFilterSelected = { viewModel.onTimeFilterSelected(it) }
                            )
                        }

                        // 2. Summary Cards
                        item { SummaryGrid(stats.summary) }

                        // 3. Fuel Stats Section
                        item { SectionTitle("Ανάλυση Καυσίμου") }
                        items(stats.fuelStats) { fuelGroup ->
                            FuelAnalysisCard(fuelGroup)
                        }
                        
                        // New: Raw Fuel Distribution Pie
                        item {
                            AnalysisCard(title = "Κατανομή ανά Τύπο Καυσίμου") {
                                RawFuelPieChart(stats.fuelDistributionRaw)
                            }
                        }

                        // Card A: Combined Chart
                        item {
                            AnalysisCard(title = "Κόστος ανά Λίτρο & Τιμή") {
                                CombinedFuelChart(stats.charts.costPerLiterPerFillUp, stats.charts.fuelPriceTrend)
                            }
                        }

                        // 4. Maintenance
                        item { MaintenanceCard(stats.expenseStats) }

                        // 5. Usage Prediction
                        item { UsageCard(stats.usageStats) }

                        // 6. Financial Analysis
                        item { SectionTitle("Οικονομική Ανάλυση") }
                        
                        item {
                            AnalysisCard(title = "Κατανομή Εξόδων (%)") {
                                ExpensePieChart(stats.expenseStats.categoryPercentages)
                            }
                        }

                        item {
                            AnalysisCard(title = "Κόστος ανά Κατηγορία (€)") {
                                HorizontalBarChart(stats.expenseStats.categoryBreakdown)
                            }
                        }

                        // 7. Trends
                        item { SectionTitle("Απόδοση & Τάσεις") }
                        
                        item {
                            AnalysisCard(title = "Τάση Κατανάλωσης (L/100km)") {
                                SimpleLineChart(stats.charts.consumptionTrend, Color.Yellow)
                            }
                        }

                        item {
                            AnalysisCard(title = "Μηνιαία Έξοδα") {
                                StackedMonthlyChart(stats.charts.monthlyExpenses)
                            }
                        }

                        // 8. Insights
                        if (stats.insights.isNotEmpty()) {
                            item { InsightsSection(stats.insights) }
                        }
                        
                        item { Spacer(modifier = Modifier.height(48.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun AnalysisCard(title: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black, spotColor = Color.Black)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(18.dp))
            content()
        }
    }
}

// --- Specialized Charts ---

@Composable
fun RawFuelPieChart(data: Map<String, Double>) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val colors = listOf(Color(0xFF00E5FF), Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF3D00), Color(0xFFD500F9))
    
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(110.dp)) {
            var startAngle = -90f
            sortedData.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second.toFloat() / 100f) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
            // Cutout for donut look
            drawCircle(Color.Transparent, radius = size.width / 3, blendMode = BlendMode.Clear)
        }
        Spacer(Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            sortedData.forEachIndexed { index, pair ->
                LegendItem(label = "${pair.first}: ${pair.second.toInt()}%", color = colors[index % colors.size])
            }
        }
    }
}

@Composable
fun ExpensePieChart(data: Map<RecordExpenseCategory, Double>) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val colors = listOf(Color(0xFF2979FF), Color(0xFF00E676), Color(0xFFFFC400), Color(0xFFFF1744), Color(0xFF00E5FF), Color(0xFFF50057))
    
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(110.dp)) {
            var startAngle = -90f
            sortedData.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second.toFloat() / 100f) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }
        Spacer(Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            sortedData.take(6).forEachIndexed { index, pair ->
                LegendItem(label = "${pair.first.name}: ${pair.second.toInt()}%", color = colors[index % colors.size])
            }
        }
    }
}

@Composable
fun CombinedFuelChart(costPerL: List<DataPoint>, priceTrend: List<DataPoint>) {
    if (costPerL.isEmpty()) return
    val maxCost = costPerL.maxOf { it.value }.toFloat()
    val maxPrice = priceTrend.maxOfOrNull { it.value }?.toFloat() ?: 1f
    val ceiling = kotlin.math.max(maxCost, maxPrice) * 1.1f

    Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        val spacing = size.width / (costPerL.size.coerceAtLeast(1))
        val barWidth = spacing * 0.35f
        
        // Bars for cost per L
        costPerL.forEachIndexed { i, dp ->
            val x = i * spacing + (spacing / 2)
            val barHeight = (dp.value.toFloat() / ceiling) * size.height
            drawRect(
                color = Color(0xFF2979FF).copy(alpha = 0.5f),
                topLeft = Offset(x - barWidth/2, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
        }

        // Line for price trend
        if (priceTrend.size > 1) {
            val path = Path()
            priceTrend.forEachIndexed { i, dp ->
                val x = i * spacing + (spacing / 2)
                val y = size.height - (dp.value.toFloat() / ceiling) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(Color.White, radius = 3.5f, center = Offset(x, y))
            }
            drawPath(path, Color(0xFFFFEA00), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        }
    }
}

@Composable
fun StackedMonthlyChart(monthlyData: List<MonthlyExpensePoint>) {
    if (monthlyData.isEmpty()) return
    val maxTotal = monthlyData.maxOf { it.total }.toFloat()
    val ceiling = maxTotal * 1.1f

    Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        val spacing = size.width / (monthlyData.size.coerceAtLeast(1))
        val barWidth = spacing * 0.5f

        monthlyData.forEachIndexed { i, data ->
            val x = i * spacing + (spacing / 2)
            val fH = (data.fuelCost.toFloat() / ceiling) * size.height
            val oH = (data.otherExpenses.toFloat() / ceiling) * size.height
            
            drawRect(Color(0xFF2196F3), Offset(x - barWidth/2, size.height - fH), Size(barWidth, fH))
            drawRect(Color(0xFFFF9800), Offset(x - barWidth/2, size.height - fH - oH), Size(barWidth, oH))
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        LegendItem("Καύσιμα", Color(0xFF2196F3))
        Spacer(Modifier.width(16.dp))
        LegendItem("Λοιπά", Color(0xFFFF9800))
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
fun HorizontalBarChart(data: Map<RecordExpenseCategory, Double>) {
    val sortedData = data.filter { it.value > 0 }.toList().sortedByDescending { it.second }
    val maxVal = sortedData.maxOfOrNull { it.second } ?: 1.0
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sortedData.forEach { (cat, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cat.name, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, modifier = Modifier.width(75.dp))
                Box(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Box(
                        Modifier.fillMaxWidth((value / maxVal).toFloat().coerceIn(0.05f, 1f))
                                .height(10.dp)
                                .background(Brush.horizontalGradient(listOf(Color(0xFF2979FF), Color(0xFF00E5FF))), RoundedCornerShape(5.dp))
                    )
                }
                Text("${value.toInt()}€", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SimpleLineChart(points: List<DataPoint>, color: Color) {
    if (points.isEmpty()) return
    val maxVal = points.maxOf { it.value }.toFloat()
    val minVal = points.minOf { it.value }.toFloat()
    val range = (maxVal - minVal).coerceAtLeast(0.1f)
    
    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val spacing = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        points.forEachIndexed { i, dp ->
            val x = i * spacing
            val y = size.height - ((dp.value.toFloat() - minVal) / range) * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            drawCircle(Color.White, radius = 3f, center = Offset(x, y))
        }
        drawPath(path, color, style = Stroke(width = 3f, cap = StrokeCap.Round))
    }
}

// --- Helper Layout Components ---

@Composable
fun SummaryGrid(summary: SummaryStats) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SummaryCard("Συνολικό Κόστος", String.format("%.2f €", summary.totalCost), Modifier.weight(1f))
            SummaryCard("Κόστος / χλμ", String.format("%.3f €/km", summary.costPerKm), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SummaryCard("Μέση Κατανάλωση", String.format("%.1f L/100", summary.averageConsumption), Modifier.weight(1f))
            SummaryCard("Διανυθέντα χλμ", "${summary.totalDistance} km", Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Text(value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun FuelAnalysisCard(group: FuelTypeGroupStats) {
    AnalysisCard(title = "Ομάδα: ${group.fuelType}") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FuelInfoItem("Μέση Καταν.", String.format("%.1f L", group.averageConsumption))
                FuelInfoItem("Μέση Τιμή", String.format("%.3f €", group.averagePricePerLiter))
                FuelInfoItem("Κόστος/100km", String.format("%.2f €", group.costPer100Km))
            }
            LinearProgressIndicator(
                progress = { (group.percentageOfTotalFuel / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color.Yellow,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Text("Σύνολο: ${group.totalLiters} Liters", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
    }
}

@Composable
fun MaintenanceCard(stats: ExpenseAnalysis) {
    AnalysisCard(title = "Δαπάνες & Συντήρηση") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MaintenanceInfoRow("Τελευταίο Service", "${stats.lastServiceDaysAgo ?: "?"} ημ / ${stats.lastServiceKmAgo ?: "?"} χλμ")
            MaintenanceInfoRow("Επόμενο Service (πρόβλεψη)", "${stats.nextServicePredictionMonths ?: "N/A"} μήνες")
        }
    }
}

@Composable
fun MaintenanceInfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun UsageCard(usage: UsageStats) {
    AnalysisCard(title = "Χρήση & Προβλέψεις") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Ημερήσια: ${String.format("%.1f", usage.averageDailyDistance)} km", color = Color.White, fontSize = 13.sp)
                Text("Εβδομαδιαία: ${String.format("%.1f", usage.averageWeeklyDistance)} km", color = Color.White, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Ετήσια: ${usage.predictedYearlyDistance} km", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Ετήσιο Κόστος: ${String.format("%.0f", usage.predictedYearlyCost)} €", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun InsightsSection(insights: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        insights.forEach { insight ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text(insight, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun FuelInfoItem(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimeFilterSection(currentFilter: TimeFilter, onFilterSelected: (TimeFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeFilter.values().forEach { filter ->
            val isSelected = currentFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name.lowercase().replace("_", " ").capitalize(), fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White.copy(alpha = 0.1f),
                    selectedContainerColor = Color.Yellow.copy(alpha = 0.5f),
                    labelColor = Color.White,
                    selectedLabelColor = Color.Black
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}
