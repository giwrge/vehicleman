package com.vehicleman.domain.use_case.record_ai

import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ParseSmartTitleUseCase {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    operator fun invoke(raw: String): ParsedInput {
        if (raw.isBlank()) return ParsedInput(raw = raw)

        val normalized = normalize(raw)

        val tokens = normalized.split(" ").filter { it.isNotBlank() }

        // --- DETECT FLAGS ---
        val isFuel = detectFuel(tokens)
        val isService = detectService(tokens)
        val isTires = detectTires(tokens)
        val isLegal = detectLegal(tokens)
        val isEV = detectEV(tokens)

        // --- DETECT VALUES (fuel only) ---
        val fuelType = detectFuelType(tokens)
        val cost = detectCost(tokens)
        val price = detectPrice(tokens)
        val quantity = detectQuantity(tokens)

        // --- DETECT DATE ---
        val parsedDate = detectDate(tokens)
        val isFuture = parsedDate?.after(Date()) == true

        // --- SERVICE ITEMS ---
        val serviceItems = detectServiceParts(tokens)

        // --- REMINDER LOGIC ---
        val isReminder =
            isFuture || containsReminderKeywords(tokens)

        return ParsedInput(
            raw = raw,
            isFuel = isFuel,
            isService = isService,
            isTires = isTires,
            isLegal = isLegal,
            isEV = isEV,
            isReminder = isReminder,
            detectedFuelType = fuelType,
            detectedQuantity = quantity,
            detectedPrice = price,
            detectedCost = cost,
            detectedServiceItems = serviceItems,
            detectedDate = parsedDate,
            detectedFuture = isFuture
        )
    }

    // ----------------------------------------------------------
    // NORMALIZATION
    // ----------------------------------------------------------

    private fun normalize(text: String): String {
        val noAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .lowercase(Locale.getDefault())

        return noAccents
            .replace("[^a-z0-9α-ω. ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    // ----------------------------------------------------------
    // DETECT FLAGS
    // ----------------------------------------------------------

    private fun detectFuel(tokens: List<String>): Boolean {
        val fuelWords = listOf(
            "fuel","gas","gasoline","petrol","unleaded",
            "benzini","βενζινη","diesel","dizel","υγραεριο","lpg","cng","95","100"
        )
        return tokens.any { it in fuelWords }
    }

    private fun detectService(tokens: List<String>): Boolean {
        val serviceWords = listOf("service","servis","λαδια","ladia","μπουζι","bouzi","filtra","φιλτρα")
        return tokens.any { it in serviceWords }
    }

    private fun detectTires(tokens: List<String>): Boolean {
        val keywords = listOf("tires","tyres","ελαστικα","lastixa","zantes")
        return tokens.any { it in keywords }
    }

    private fun detectLegal(tokens: List<String>): Boolean {
        val keywords = listOf("τελη","teli","ασφαλεια","insurance","diodia")
        return tokens.any { it in keywords }
    }

    private fun detectEV(tokens: List<String>): Boolean {
        val keywords = listOf("charge","ev","φορτιση")
        return tokens.any { it in keywords }
    }

    private fun containsReminderKeywords(tokens: List<String>): Boolean {
        val k = listOf("kteo","ασφαλεια","τελη","mot","service","ραντεβου","appointment")
        return tokens.any { it in k }
    }

    // ----------------------------------------------------------
    // FUEL DETAILS
    // ----------------------------------------------------------

    private fun detectFuelType(tokens: List<String>): String? {
        return when {
            tokens.contains("100") -> "unleaded_100"
            tokens.contains("98") -> "unleaded_98"
            tokens.contains("95") -> "unleaded_95"
            tokens.contains("diesel") || tokens.contains("dizel") || tokens.contains("πετρελαιο") -> "diesel"
            tokens.contains("lpg") || tokens.contains("υγραεριο") -> "lpg"
            else -> null
        }
    }

    private fun detectCost(tokens: List<String>): Double? {
        val euroToken = tokens.find { it.endsWith("e") || it.endsWith("€") }
        return euroToken?.replace("e","")?.replace("€","")?.toDoubleOrNull()
    }

    private fun detectPrice(tokens: List<String>): Double? {
        return tokens.firstOrNull { it.contains('.') && it.length <= 6 }
            ?.toDoubleOrNull()
    }

    private fun detectQuantity(tokens: List<String>): Double? {
        val ltToken = tokens.find { it.endsWith("lt") || it.endsWith("l") }
        return ltToken?.replace("lt","")?.replace("l","")?.toDoubleOrNull()
    }

    // ----------------------------------------------------------
    // DATE PARSER
    // ----------------------------------------------------------

    private fun detectDate(tokens: List<String>): Date? {
        tokens.forEach { t ->
            if (t.count { it == '/' } == 2) {
                return try { dateFormat.parse(t) } catch (e: Exception) { null }
            }
        }
        return null
    }

    // ----------------------------------------------------------
    // SERVICE PARTS
    // ----------------------------------------------------------

    private fun detectServiceParts(tokens: List<String>): List<String> {
        val mapping = mapOf(
            "λαδια" to "Oil Change",
            "ladia" to "Oil Change",
            "μπουζι" to "Spark Plugs",
            "bouzi" to "Spark Plugs",
            "filtra" to "Filters",
            "φιλτρο" to "Filters",
            "air" to "Air Filter",
            "cabin" to "Cabin Filter"
        )
        return tokens.mapNotNull { mapping[it] }
    }
}
