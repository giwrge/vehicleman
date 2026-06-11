package com.vehicleman.domain.model

import java.util.Date

data class DetailedStatistics(
    val summary: SummaryStats,
    val fuelStats: List<FuelTypeGroupStats>,
    val fuelDistributionRaw: Map<String, Double>, 
    val expenseStats: ExpenseAnalysis,
    val usageStats: UsageStats,
    val charts: ChartData,
    val insights: List<String>
)

data class SummaryStats(
    val totalCost: Double,
    val costPerKm: Double,
    val averageConsumption: Double, 
    val totalDistance: Int
)

data class FuelTypeGroupStats(
    val fuelType: String,
    val averageConsumption: Double,
    val averagePricePerLiter: Double,
    val costPer100Km: Double,
    val totalLiters: Double,
    val percentageOfTotalFuel: Double
)

data class ExpenseAnalysis(
    val categoryBreakdown: Map<RecordExpenseCategory, Double>,
    val categoryPercentages: Map<RecordExpenseCategory, Double>,
    val lastServiceDaysAgo: Int?,
    val lastServiceKmAgo: Int?,
    val nextServicePredictionMonths: Int?
)

data class UsageStats(
    val averageDailyDistance: Double,
    val averageWeeklyDistance: Double,
    val predictedYearlyDistance: Int,
    val predictedYearlyCost: Double
)

data class ChartData(
    val fuelPriceTrend: List<DataPoint>,
    val consumptionTrend: List<DataPoint>,
    val monthlyExpenses: List<MonthlyExpensePoint>,
    val costPerLiterPerFillUp: List<DataPoint>
)

data class DataPoint(
    val date: Date,
    val value: Double,
    val label: String? = null
)

data class MonthlyExpensePoint(
    val month: String,
    val fuelCost: Double,
    val otherExpenses: Double,
    val total: Double
)
