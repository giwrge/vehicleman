package com.vehicleman.domain.util

import com.vehicleman.domain.model.*
import java.util.*
import java.util.concurrent.TimeUnit

data class CalculatedStats(
    val averageConsumptionLitersPer100km: Double,
    val averageCostPer100km: Double,
    val estimatedMonthlyKm: Double,
    val estimatedYearlyKm: Double,
    val estimatedMonthlyCost: Double,
    val estimatedYearlyCost: Double,
    val totalFuelCost: Double,
    val totalServiceCost: Double,
    val totalInsuranceCost: Double,
    val totalTaxCost: Double,
    val totalOtherExpenses: Double
)

object VehicleStatisticsCalculator {

    fun calculate(records: List<Record>): CalculatedStats {
        val fuelRecords = records.filter { it.recordType == RecordType.FUEL_UP }.sortedBy { it.date }
        val serviceRecords = records.filter { it.category == RecordExpenseCategory.SERVICE }
        val insuranceRecords = records.filter { it.category == RecordExpenseCategory.INSURANCE }
        val taxRecords = records.filter { it.category == RecordExpenseCategory.TAXES }
        val otherRecords = records.filter { 
            (it.recordType == RecordType.EXPENSE || it.recordType == RecordType.REMINDER && it.isCompleted) && 
            it.category !in listOf(RecordExpenseCategory.SERVICE, RecordExpenseCategory.INSURANCE, RecordExpenseCategory.TAXES, RecordExpenseCategory.FUEL) 
        }

        val totalFuelCost = fuelRecords.sumOf { it.cost ?: 0.0 }
        val totalServiceCost = serviceRecords.sumOf { it.cost ?: 0.0 }
        val totalInsuranceCost = insuranceRecords.sumOf { it.cost ?: 0.0 }
        val totalTaxCost = taxRecords.sumOf { it.cost ?: 0.0 }
        val totalOtherExpenses = otherRecords.sumOf { (it.cost ?: 0.0) + (it.costReminder ?: 0.0) }

        val totalDistance = if (fuelRecords.size >= 2) {
            fuelRecords.last().odometer - fuelRecords.first().odometer
        } else 0
        
        val totalLiters = fuelRecords.drop(1).sumOf { it.quantity ?: 0.0 }
        val averageConsumption = if (totalDistance > 0) (totalLiters / totalDistance) * 100 else 0.0
        val averageCostPer100km = if (totalDistance > 0) (totalFuelCost / totalDistance) * 100 else 0.0

        val durationDays = getDurationInDays(records).toDouble().coerceAtLeast(1.0)
        val dailyKm = totalDistance / durationDays
        val monthlyKm = dailyKm * 30.44
        val yearlyKm = dailyKm * 365.25

        val totalCost = totalFuelCost + totalServiceCost + totalInsuranceCost + totalTaxCost + totalOtherExpenses
        val dailyCost = totalCost / durationDays
        val monthlyCost = dailyCost * 30.44
        val yearlyCost = dailyCost * 365.25

        return CalculatedStats(
            averageConsumptionLitersPer100km = averageConsumption,
            averageCostPer100km = averageCostPer100km,
            estimatedMonthlyKm = monthlyKm,
            estimatedYearlyKm = yearlyKm,
            estimatedMonthlyCost = monthlyCost,
            estimatedYearlyCost = yearlyCost,
            totalFuelCost = totalFuelCost,
            totalServiceCost = totalServiceCost,
            totalInsuranceCost = totalInsuranceCost,
            totalTaxCost = totalTaxCost,
            totalOtherExpenses = totalOtherExpenses
        )
    }

    fun calculateDetailed(records: List<Record>, timeFilter: TimeFilter): DetailedStatistics {
        val filteredRecords = filterRecordsByTime(records, timeFilter)
        val sortedRecords = filteredRecords.sortedBy { it.date }
        
        val fuelRecords = sortedRecords.filter { it.recordType == RecordType.FUEL_UP }
        val expenseRecords = sortedRecords.filter { it.recordType == RecordType.EXPENSE }
        
        // 1. Summary Stats
        val totalCost = sortedRecords.sumOf { (it.cost ?: 0.0) + (it.costReminder ?: 0.0) }
        val totalDistance = if (sortedRecords.size > 1) {
            sortedRecords.last().odometer - sortedRecords.first().odometer
        } else 0
        
        val totalLiters = fuelRecords.sumOf { it.quantity ?: 0.0 }
        val averageConsumption = if (totalDistance > 0) (totalLiters / totalDistance) * 100 else 0.0
        val costPerKm = if (totalDistance > 0) totalCost / totalDistance else 0.0

        val summary = SummaryStats(
            totalCost = totalCost,
            costPerKm = costPerKm,
            averageConsumption = averageConsumption,
            totalDistance = totalDistance
        )

        // 2. Fuel Stats (Grouped for Analysis)
        val groupedFuelStats = fuelRecords.groupBy { normalizeFuelType(it.fuelType) }
            .map { (groupName, groupRecords) ->
                val groupLiters = groupRecords.sumOf { it.quantity ?: 0.0 }
                val groupCost = groupRecords.sumOf { it.cost ?: 0.0 }
                val groupFirstOdo = groupRecords.minOfOrNull { it.odometer } ?: 0
                val groupLastOdo = groupRecords.maxOfOrNull { it.odometer } ?: 0
                val groupDist = groupLastOdo - groupFirstOdo
                
                FuelTypeGroupStats(
                    fuelType = groupName,
                    averageConsumption = if (groupDist > 0) (groupLiters / groupDist) * 100 else 0.0,
                    averagePricePerLiter = if (groupLiters > 0) groupCost / groupLiters else 0.0,
                    costPer100Km = if (groupDist > 0) (groupCost / groupDist) * 100 else 0.0,
                    totalLiters = groupLiters,
                    percentageOfTotalFuel = if (totalLiters > 0) (groupLiters / totalLiters) * 100 else 0.0
                )
            }

        // Raw Fuel Distribution (for individual Pie chart)
        val fuelDistributionRaw = fuelRecords.groupBy { it.fuelType ?: "Άγνωστο" }
            .mapValues { it.value.sumOf { r -> r.quantity ?: 0.0 } }
            .mapValues { if (totalLiters > 0) (it.value / totalLiters) * 100 else 0.0 }

        // 3. Expense Analysis
        val categoryBreakdown = expenseRecords.groupBy { it.category ?: RecordExpenseCategory.OTHER }
            .mapValues { it.value.sumOf { record -> record.cost ?: 0.0 } }
            .toMutableMap()
        
        categoryBreakdown[RecordExpenseCategory.FUEL] = fuelRecords.sumOf { it.cost ?: 0.0 }
        
        val totalExpenses = categoryBreakdown.values.sum()
        val categoryPercentages = categoryBreakdown.mapValues { if (totalExpenses > 0) (it.value / totalExpenses) * 100 else 0.0 }

        val lastService = sortedRecords.filter { it.category == RecordExpenseCategory.SERVICE }.lastOrNull()
        val lastServiceDaysAgo = lastService?.let { 
            val diff = Date().time - it.date.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }
        val lastServiceKmAgo = lastService?.let {
            val latestOdo = sortedRecords.lastOrNull()?.odometer ?: 0
            latestOdo - it.odometer
        }

        val expenseAnalysis = ExpenseAnalysis(
            categoryBreakdown = categoryBreakdown,
            categoryPercentages = categoryPercentages,
            lastServiceDaysAgo = lastServiceDaysAgo,
            lastServiceKmAgo = lastServiceKmAgo,
            nextServicePredictionMonths = calculateNextServicePrediction(sortedRecords)
        )

        // 4. Usage Stats
        val durationInDays = getDurationInDays(sortedRecords).toDouble().coerceAtLeast(1.0)
        val avgDaily = totalDistance / durationInDays
        val usage = UsageStats(
            averageDailyDistance = avgDaily,
            averageWeeklyDistance = avgDaily * 7,
            predictedYearlyDistance = (avgDaily * 365).toInt(),
            predictedYearlyCost = (totalCost / durationInDays) * 365
        )

        // 5. Chart Data
        val charts = ChartData(
            fuelPriceTrend = fuelRecords.map { DataPoint(it.date, it.pricePerUnit ?: 0.0) },
            consumptionTrend = calculateConsumptionTrend(fuelRecords),
            monthlyExpenses = calculateMonthlyExpenses(sortedRecords),
            costPerLiterPerFillUp = fuelRecords.map { DataPoint(it.date, (it.cost ?: 0.0) / (it.quantity ?: 1.0)) }
        )

        // 6. Insights
        val insights = mutableListOf<String>()
        if (expenseAnalysis.nextServicePredictionMonths != null) {
            insights.add("Με βάση τη χρήση σας, το επόμενο Service αναμένεται σε ${expenseAnalysis.nextServicePredictionMonths} μήνες.")
        }
        
        return DetailedStatistics(
            summary = summary,
            fuelStats = groupedFuelStats,
            fuelDistributionRaw = fuelDistributionRaw,
            expenseStats = expenseAnalysis,
            usageStats = usage,
            charts = charts,
            insights = insights
        )
    }

    private fun normalizeFuelType(type: String?): String {
        if (type == null) return "Άλλο"
        val lower = type.lowercase()
        return when {
            lower.contains("βενζίνη") || lower.contains("gasoline") || lower.contains("95") || lower.contains("98") || lower.contains("100") -> "Βενζίνη"
            lower.contains("diesel") || lower.contains("πετρέλαιο") -> "Πετρέλαιο"
            lower.contains("lpg") || lower.contains("υγραέριο") -> "LPG"
            lower.contains("cng") -> "CNG"
            else -> type
        }
    }

    private fun filterRecordsByTime(records: List<Record>, filter: TimeFilter): List<Record> {
        if (filter == TimeFilter.SUMMARY) return records
        val cutoff = Calendar.getInstance()
        when (filter) {
            TimeFilter.LAST_WEEK -> cutoff.add(Calendar.DAY_OF_YEAR, -7)
            TimeFilter.LAST_MONTH -> cutoff.add(Calendar.MONTH, -1)
            TimeFilter.SIX_MONTHS -> cutoff.add(Calendar.MONTH, -6)
            TimeFilter.YEAR -> cutoff.add(Calendar.YEAR, -1)
            else -> return records
        }
        return records.filter { it.date.after(cutoff.time) }
    }

    private fun getDurationInDays(records: List<Record>): Long {
        if (records.size < 2) return 1
        val first = records.minOf { it.date.time }
        val last = records.maxOf { it.date.time }
        val diff = last - first
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).coerceAtLeast(1)
    }

    private fun calculateConsumptionTrend(fuelRecords: List<Record>): List<DataPoint> {
        val points = mutableListOf<DataPoint>()
        if (fuelRecords.size < 2) return emptyList()
        for (i in 0 until fuelRecords.size - 1) {
            val dist = fuelRecords[i+1].odometer - fuelRecords[i].odometer
            if (dist > 0) {
                val consumption = (fuelRecords[i+1].quantity ?: 0.0) / dist * 100
                points.add(DataPoint(fuelRecords[i+1].date, consumption))
            }
        }
        return points
    }

    private fun calculateMonthlyExpenses(records: List<Record>): List<MonthlyExpensePoint> {
        val calendar = Calendar.getInstance()
        return records.groupBy { 
            calendar.time = it.date
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
        }.map { (month, monthRecords) ->
            val fuel = monthRecords.filter { it.recordType == RecordType.FUEL_UP }.sumOf { it.cost ?: 0.0 }
            val other = monthRecords.filter { it.recordType != RecordType.FUEL_UP }.sumOf { (it.cost ?: 0.0) + (it.costReminder ?: 0.0) }
            MonthlyExpensePoint(month, fuel, other, fuel + other)
        }.sortedBy { it.month }
    }

    private fun calculateNextServicePrediction(records: List<Record>): Int? {
        // Placeholder prediction logic
        return 6
    }
}
