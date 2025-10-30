package com.vehicleman.domain.util

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import java.util.concurrent.TimeUnit

data class FuelStats(
    val fuelType: String,
    val averageConsumption: Double = 0.0,
    val averageCostPer100km: Double = 0.0
)

data class CalculatedStats(
    // Overall
    val averageConsumptionLitersPer100km: Double = 0.0,
    val averageCostPer100km: Double = 0.0,
    
    // Estimates
    val estimatedMonthlyKm: Double = 0.0,
    val estimatedYearlyKm: Double = 0.0,
    val estimatedMonthlyCost: Double = 0.0,
    val estimatedYearlyCost: Double = 0.0,

    // Costs by Category
    val totalFuelCost: Double = 0.0,
    val totalServiceCost: Double = 0.0,
    val totalInsuranceCost: Double = 0.0,
    val totalTaxCost: Double = 0.0,
    val totalOtherExpenses: Double = 0.0,

    // Stats per Fuel Type
    val statsByFuelType: List<FuelStats> = emptyList()
)

object VehicleStatisticsCalculator {

    fun calculate(records: List<Record>): CalculatedStats {
        val fuelRecords = records.filter { it.recordType == RecordType.FUEL_UP && it.quantity != null && it.odometer > 0 }.sortedBy { it.odometer }
        val expenseRecords = records.filter { it.recordType == RecordType.EXPENSE && it.cost != null }
        
        // --- Total Distance and Time Calculation ---
        val allOdometerRecords = records.filter { it.odometer > 0 }.sortedBy { it.odometer }
        val firstRecord = allOdometerRecords.firstOrNull()
        val lastRecord = allOdometerRecords.lastOrNull()

        var totalDistanceOverall = 0
        var durationInDays = 1L

        if (firstRecord != null && lastRecord != null && firstRecord.id != lastRecord.id) {
            totalDistanceOverall = lastRecord.odometer - firstRecord.odometer
            val diffInMillis = kotlin.math.abs(lastRecord.date.time - firstRecord.date.time)
            durationInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS).coerceAtLeast(1)
        }

        // --- Overall Fuel Consumption & Cost ---
        var totalLitersOverall = 0.0
        var totalFuelCostOverall = 0.0

        if (fuelRecords.size > 1) {
            for (i in 0 until fuelRecords.size - 1) {
                val distance = fuelRecords[i+1].odometer - fuelRecords[i].odometer
                if (distance > 0) {
                    totalLitersOverall += fuelRecords[i+1].quantity!!
                    totalFuelCostOverall += fuelRecords[i+1].cost!!
                }
            }
        }
        val avgConsumption = if (totalDistanceOverall > 0) (totalLitersOverall / totalDistanceOverall) * 100 else 0.0
        val avgCost = if (totalDistanceOverall > 0) (totalFuelCostOverall / totalDistanceOverall) * 100 else 0.0

        // --- Estimations ---
        val avgKmPerDay = if (durationInDays > 0) totalDistanceOverall.toDouble() / durationInDays else 0.0
        val totalCosts = records.sumOf { it.cost ?: 0.0 }
        val avgCostPerDay = if (durationInDays > 0) totalCosts / durationInDays else 0.0
        
        // --- Costs by Category ---
        val totalFuelCost = expenseRecords.filter { it.recordType == RecordType.FUEL_UP }.sumOf { it.cost!! }
        val totalServiceCost = expenseRecords.filter { it.description?.contains("service", true) == true }.sumOf { it.cost!! }
        val totalInsuranceCost = expenseRecords.filter { it.description?.contains("insurance", true) == true || it.description?.contains("ασφάλεια", true) == true }.sumOf { it.cost!! }
        val totalTaxCost = expenseRecords.filter { it.description?.contains("tax", true) == true || it.description?.contains("τέλη", true) == true }.sumOf { it.cost!! }
        val otherCost = expenseRecords.sumOf { it.cost!! } - (totalServiceCost + totalInsuranceCost + totalTaxCost)

        // --- Stats per Fuel Type ---
        val statsByFuel = fuelRecords.groupBy { it.fuelType!! }.map { (type, recordsForType) ->
            var distanceForType = 0
            var litersForType = 0.0
            var costForType = 0.0
            if (recordsForType.size > 1) {
                 for (i in 0 until recordsForType.size - 1) {
                    val dist = recordsForType[i+1].odometer - recordsForType[i].odometer
                    if (dist > 0) {
                        distanceForType += dist
                        litersForType += recordsForType[i+1].quantity!!
                        costForType += recordsForType[i+1].cost!!
                    }
                }
            }
            FuelStats(
                fuelType = type,
                averageConsumption = if(distanceForType > 0) (litersForType/distanceForType) * 100 else 0.0,
                averageCostPer100km = if(distanceForType > 0) (costForType/distanceForType) * 100 else 0.0
            )
        }

        return CalculatedStats(
            averageConsumptionLitersPer100km = avgConsumption,
            averageCostPer100km = avgCost,
            estimatedMonthlyKm = avgKmPerDay * 30.44,
            estimatedYearlyKm = avgKmPerDay * 365.25,
            estimatedMonthlyCost = avgCostPerDay * 30.44,
            estimatedYearlyCost = avgCostPerDay * 365.25,
            totalFuelCost = totalFuelCost,
            totalServiceCost = totalServiceCost,
            totalInsuranceCost = totalInsuranceCost,
            totalTaxCost = totalTaxCost,
            totalOtherExpenses = otherCost,
            statsByFuelType = statsByFuel
        )
    }
}