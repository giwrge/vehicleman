package com.vehicleman.presentation.addeditrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.GetLatestOdometer
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillExpenseDataUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillFuelDataUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillReminderDataUseCase
import com.vehicleman.domain.use_case.record_ai.ExpenseAutofillResult
import com.vehicleman.domain.use_case.record_ai.FuelAutofillRequest
import com.vehicleman.domain.use_case.record_ai.FuelTypeHint
import com.vehicleman.domain.use_case.record_ai.GenerateSmartSuggestionsUseCase
import com.vehicleman.domain.use_case.record_ai.ParseSmartTitleUseCase
import com.vehicleman.domain.use_case.record_ai.PredictRecordTypeFromParsedDataUseCase
import com.vehicleman.domain.use_case.record_ai.RecordTypeHint
import com.vehicleman.domain.use_case.record_ai.ReminderAutofillRequest
import com.vehicleman.domain.use_case.record_ai.ReminderAutofillResult
import com.vehicleman.domain.use_case.record_ai.SuggestionsRequest
import com.vehicleman.domain.use_case.record_ai.GetLastFuelUpRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val vehicleRepository: VehicleRepository,
    private val categorizer: RecordCategorizerUseCase,
    // 🔥 AI use cases
    private val parseSmartTitle: ParseSmartTitleUseCase,
    private val predictRecordType: PredictRecordTypeFromParsedDataUseCase,
    private val autoFillFuel: AutoFillFuelDataUseCase,
    private val autoFillExpense: AutoFillExpenseDataUseCase,
    private val autoFillReminder: AutoFillReminderDataUseCase,
    private val generateSuggestions: GenerateSmartSuggestionsUseCase,
    private val getLatestOdometer: GetLatestOdometer,
    private val getLastFuelUpRecord: GetLastFuelUpRecord,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditRecordState())
    val state = _state.asStateFlow()

    private val vehicleId: String =
        savedStateHandle.get<String>("vehicleId") ?: ""

    private val recordId: String? =
        savedStateHandle.get<String>("recordId")

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    // 🔥 Cache για AI
    private var lastOdometerKm: Int? = null
    private var recentRecords: List<Record> = emptyList()

    init {
        if (vehicleId.isBlank()) {
            // Δεν μπορούμε να συνεχίσουμε χωρίς vehicleId
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Λείπει το όχημα για την εγγραφή."
                )
            }
        } else {
            _state.update { it.copy(vehicleId = vehicleId) }

            // 🔥 Φόρτωση latest odometer & recent records (μία φορά)
            viewModelScope.launch {
                val lastFuelRecord = getLastFuelUpRecord(vehicleId)
                lastOdometerKm = lastFuelRecord?.odometer
                recentRecords = recordRepository.getRecordsByVehicle(vehicleId)
            }


            if (recordId == null || recordId == "new") {
                loadNewRecord()
            } else {
                loadExistingRecord(recordId)
            }

            loadVehicleFuelTypes()
        }
    }

    // -------------------------------------------------------------------------
    // LOAD VEHICLE FUEL TYPES (προς το παρόν κενό, μέχρι να μπουν τα fields στο Vehicle)
    // -------------------------------------------------------------------------

    private fun loadVehicleFuelTypes() {
        viewModelScope.launch {
            // Όταν προσθέσουμε στο Vehicle π.χ. primaryFuelType / supportedFuelTypes,
            // θα γεμίσουμε εδώ την λίστα.
            _state.update { s ->
                s.copy(
                    vehicleFuelTypes = emptyList(),
                    selectedFuelType = null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // NEW RECORD
    // -------------------------------------------------------------------------

    private fun loadNewRecord() {
        val now = Date()
        _state.update {
            it.copy(
                isLoading = false,
                isNew = true,
                date = now,
                dateText = dateFormat.format(now),
                category = null,
                suggestions = emptyList()
            )
        }
    }

    // -------------------------------------------------------------------------
    // LOAD EXISTING RECORD
    // -------------------------------------------------------------------------

    private fun loadExistingRecord(id: String) {
        viewModelScope.launch {
            val record = recordRepository.getRecordById(id)

            if (record == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Δεν βρέθηκε η εγγραφή."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    isNew = false,
                    recordId = record.id,
                    recordType = record.recordType,
                    title = record.title,
                    description = record.description ?: "",
                    date = record.date,
                    dateText = dateFormat.format(record.date),
                    odometer = record.odometer?.toString() ?: "",
                    cost = record.cost?.toString() ?: "",
                    quantity = record.quantity?.toString() ?: "",
                    pricePerUnit = record.pricePerUnit?.toString() ?: "",
                    fuelType = record.fuelType,
                    fuelTypeText = record.fuelType ?: "",
                    isReminder = record.isReminder,
                    isCompleted = record.isCompleted,
                    reminderDate = record.reminderDate,
                    reminderDateText = record.reminderDate?.let { d -> dateFormat.format(d) } ?: "",
                    reminderOdometer = record.reminderOdometer?.toString() ?: "",
                    isReminderSwitchLocked = record.isReminder,
                    showReminderFields = record.isReminder,
                    // την κατηγορία την ξανα-υπολογίζουμε από τίτλο/περιγραφή
                    category = null,
                    suggestions = emptyList()
                )
            }

            // Υπολογίζουμε εκ νέου την κατηγορία για την υπάρχουσα εγγραφή
            updateCategorization()
        }
    }

    // -------------------------------------------------------------------------
    // INTELLIGENT DATE HANDLING (future date ⇒ reminder)
    // -------------------------------------------------------------------------

    private fun processIntelligentDate(newDate: Date) {
        val todayLocal = java.time.LocalDate.now()
        val newLocal = newDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // Αν η ημερομηνία είναι μελλοντική ⇒ ΥΠΕΝΘΥΜΙΣΗ
        if (newLocal.isAfter(todayLocal)) {
            val title = _state.value.title

            // parse τίτλο, έστω κι αν είναι απλός
            val parsed = parseSmartTitle(title, todayLocal)

            val req = ReminderAutofillRequest(
                lastOdometerKm = lastOdometerKm,
                fallbackDate = newLocal
            )

            val result: ReminderAutofillResult = autoFillReminder(parsed, req)

            val reminderDate = result.reminderDate?.let { local ->
                Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant())
            } ?: newDate

            _state.update { old ->
                old.copy(
                    recordType = RecordType.REMINDER,
                    isReminder = true,
                    showReminderFields = true,
                    isReminderSwitchLocked = true,
                    reminderDate = reminderDate,
                    reminderDateText = dateFormat.format(reminderDate),
                    reminderOdometer = if (old.reminderOdometer.isBlank() && result.reminderKm != null)
                        result.reminderKm.toString() else old.reminderOdometer,
                    description = if (old.description.isBlank() && result.autoDescription.isNotBlank())
                        result.autoDescription else old.description
                )
            }

            updateCategorization()
        }
    }

    // -------------------------------------------------------------------------
    // EVENT HANDLING
    // -------------------------------------------------------------------------

    fun onEvent(event: AddEditRecordEvent) {
        when (event) {

            is AddEditRecordEvent.LoadExisting -> {
                // Αν τυχόν το καλέσεις από UI
                loadExistingRecord(event.recordId)
            }

            is AddEditRecordEvent.TitleChanged -> {
                _state.update { it.copy(title = event.value) }
                processIntelligentTitle(event.value)
                validateSave()
            }

            is AddEditRecordEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.value) }
                updateCategorization()
                validateSave()
            }

            is AddEditRecordEvent.DateTextChanged -> {
                _state.update { it.copy(dateText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.DateChanged -> {
                _state.update {
                    it.copy(
                        date = event.value,
                        dateText = dateFormat.format(event.value)
                    )
                }
                processIntelligentDate(event.value)
                validateSave()
            }

            is AddEditRecordEvent.OdometerChanged -> {
                _state.update { it.copy(odometer = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.CostChanged -> {
                _state.update { it.copy(cost = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.QuantityChanged -> {
                _state.update { it.copy(quantity = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.PricePerUnitChanged -> {
                _state.update { it.copy(pricePerUnit = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.FuelTypeChanged -> {
                _state.update { it.copy(fuelTypeText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.RecordTypeChanged -> {
                handleRecordTypeChange(event.value)
            }

            is AddEditRecordEvent.SuggestionClicked -> {
                val current = _state.value.title
                val newTitle = if (current.isBlank()) {
                    event.suggestion
                } else {
                    (current.trimEnd() + " " + event.suggestion).trim()
                }

                _state.update { it.copy(title = newTitle) }
                processIntelligentTitle(newTitle)
                validateSave()
            }

            is AddEditRecordEvent.ToggleReminder -> {
                if (!_state.value.isReminderSwitchLocked) {
                    _state.update {
                        it.copy(
                            isReminder = event.value,
                            showReminderFields = event.value
                        )
                    }
                }
                validateSave()
            }

            is AddEditRecordEvent.ReminderDateTextChanged -> {
                _state.update { it.copy(reminderDateText = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.CostReminderChanged -> {
                _state.update { it.copy(costReminder = event.value) }
                validateSave()
            }

            is AddEditRecordEvent.ReminderDateChanged -> {
                _state.update {
                    it.copy(
                        reminderDate = event.value,
                        reminderDateText = dateFormat.format(event.value)
                    )
                }
                validateSave()
            }

            is AddEditRecordEvent.ReminderOdometerChanged -> {
                _state.update { it.copy(reminderOdometer = event.value) }
                validateSave()
            }

            AddEditRecordEvent.ToggleCompleted -> {
                _state.update { it.copy(isCompleted = !it.isCompleted) }
                validateSave()
            }

            AddEditRecordEvent.Save -> saveRecord()

            AddEditRecordEvent.ErrorShown -> {
                _state.update { it.copy(errorMessage = null) }
            }

            AddEditRecordEvent.NavigateBackConsumed -> {
                _state.update { it.copy(navigateBack = false) }
            }
        }
    }

    // -------------------------------------------------------------------------
    // 🔥 AI PIPELINE ΓΙΑ ΤΟΝ ΤΙΤΛΟ
    // -------------------------------------------------------------------------

    private fun processIntelligentTitle(newTitle: String) {
        val s = _state.value
        if (newTitle.isBlank()) {
            _state.update { it.copy(suggestions = emptyList()) }
            _state.update { it.copy(category = null) }
            return
        }

        val nowDate = s.date
        val nowLocal = nowDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // 1) Parsing του τίτλου
        val parsed = parseSmartTitle(newTitle, nowLocal)

        // 2) Τι είδος εγγραφή μοιάζει να είναι;
        val typeHint = predictRecordType(parsed)

        // 🔥 Για να μην πετάει Υπενθύμιση όταν δεν υπάρχει future date:
        // REMINDER hint → το αντιμετωπίζουμε σαν EXPENSE εδώ.
        val mappedType = when (typeHint) {
            RecordTypeHint.FUEL -> RecordType.FUEL_UP
            RecordTypeHint.REMINDER -> RecordType.EXPENSE
            RecordTypeHint.EXPENSE, RecordTypeHint.UNKNOWN -> RecordType.EXPENSE
        }

        val previousType = s.recordType

        // 3) Αν αλλάζει τύπος εγγραφής → ενημέρωσε flags / UI
        if (mappedType != previousType) {
            handleRecordTypeChange(mappedType)
        } else {
            // Αν δεν αλλάζει, τουλάχιστον κάνε recalc την κατηγορία
            updateCategorization()
        }

        // 4) Auto-fill ανάλογα με τον τύπο
        when (mappedType) {
            RecordType.FUEL_UP -> applyFuelAutofill(parsed)
            RecordType.EXPENSE -> applyExpenseAutofill(parsed)
            RecordType.REMINDER -> applyReminderAutofill(parsed, nowLocal)
        }

        // 5) Έξυπνες προτάσεις
        updateSuggestions()

        // 6) Τελικό recalc κατηγορίας (με ενημερωμένη description κλπ.)
        updateCategorization()
    }

    private fun applyFuelAutofill(parsed: ParsedSmartTitle) {

        val request = FuelAutofillRequest(
            costEuro = parsed.detectedCostEuro,
            liters = parsed.detectedLiters,
            pricePerLiter = parsed.detectedPricePerLiter,
            fuelTypeHint = parsed.detectedFuelType
        )

        val result = autoFillFuel(request)

        // Υπολογισμός km από το προηγούμενο γέμισμα
        val kmSinceLastFill = lastOdometerKm?.let { lastKm ->
            val currentKm = _state.value.odometer.toIntOrNull()
            if (currentKm != null && currentKm > lastKm) {
                currentKm - lastKm
            } else null
        }

        val autoDescription = buildString {
            append("Γέμισμα καυσίμου")
            if (result.fuelTypeHint != null) {
                append(" (${mapFuelTypeHintToString(result.fuelTypeHint)})")
            }
            if (result.liters != null && result.pricePerLiter != null) {
                append(" ${"%.2f".format(result.liters)} lt × ${result.pricePerLiter} €/lt")
            }
            if (result.costEuro != null) {
                append(" = ${result.costEuro}€")
            }
            if (kmSinceLastFill != null) {
                append(" — Διαδρομή από το προηγούμενο γέμισμα: ${kmSinceLastFill} km")
            }
        }

        _state.update { old ->
            old.copy(
                cost = result.costEuro?.toString() ?: old.cost,
                quantity = result.liters?.toString() ?: old.quantity,
                pricePerUnit = result.pricePerLiter?.toString() ?: old.pricePerUnit,
                fuelTypeText = result.fuelTypeHint?.let { mapFuelTypeHintToString(it) }
                    ?: old.fuelTypeText,
                description = autoDescription
            )
        }
    }

    private fun applyExpenseAutofill(parsed: ParsedSmartTitle) {

        val result: ExpenseAutofillResult = autoFillExpense(parsed)

        val dateText = dateFormat.format(_state.value.date)

        val autoDescription = buildString {
            append("Service / Έξοδο: ")
            append(parsed.cleanedText)

            if (result.costEuro != null) {
                append(" — Κόστος: ${result.costEuro}€")
            }

            append(" — Ημ/νία: $dateText")
        }

        _state.update { old ->
            old.copy(
                cost = result.costEuro?.toString() ?: old.cost,
                description = autoDescription
            )
        }
    }


    private fun applyReminderAutofill(
        parsed: ParsedSmartTitle,
        nowLocal: LocalDate
    ) {
        val req = ReminderAutofillRequest(
            lastOdometerKm = lastOdometerKm,
            fallbackDate = nowLocal

        )

        val result = autoFillReminder(parsed, req)

        val reminderDate = result.reminderDate?.let { local ->
            Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant())
        } ?: run {
            Date.from(nowLocal.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        val autoDescription = buildString {
            append("Υπενθύμιση: ")
            append(parsed.cleanedText)

            if (result.autoDescription.isNotBlank()) {
                append(" — ")
                append(result.autoDescription)
            }

            append(" στις ${dateFormat.format(reminderDate)}")

            if (result.reminderKm != null) {
                append(" — στα ${result.reminderKm} km")
            }
        }

        _state.update { old ->
            old.copy(
                reminderDate = reminderDate,
                reminderDateText = dateFormat.format(reminderDate),
                reminderOdometer = result.reminderKm?.toString() ?: old.reminderOdometer,
                costReminder = result.costEuro?.toString() ?: old.costReminder,          // NEW
                description = autoDescription,
                isReminder = true,
                showReminderFields = true,
                isReminderSwitchLocked = true
            )
        }
    }


    private fun mapFuelTypeHintToString(hint: FuelTypeHint): String {
        return when (hint) {
            FuelTypeHint.UNLEADED_95 -> "unleaded_95"
            FuelTypeHint.UNLEADED_100 -> "unleaded_100"
            FuelTypeHint.DIESEL -> "diesel"
            FuelTypeHint.LPG -> "lpg"
            FuelTypeHint.CNG -> "cng"
            FuelTypeHint.ELECTRIC -> "electric"
            FuelTypeHint.UNKNOWN -> ""
        }
    }

    // -------------------------------------------------------------------------
    // HANDLE RECORD TYPE CHANGE
    // -------------------------------------------------------------------------

    private fun handleRecordTypeChange(type: RecordType) {
        _state.update {
            it.copy(
                recordType = type,
                showCostDetails = type == RecordType.FUEL_UP,
                showFuelTypeSelection = type == RecordType.FUEL_UP,
                isReminder = type == RecordType.REMINDER,
                showReminderFields = type == RecordType.REMINDER,
                isReminderSwitchLocked = type == RecordType.REMINDER
            )
        }
        updateCategorization()
        validateSave()
    }

    // -------------------------------------------------------------------------
    // CATEGORIZER
    // -------------------------------------------------------------------------

    private fun updateCategorization() {
        val s = _state.value

        if (s.title.isBlank() && s.description.isBlank()) {
            _state.update { it.copy(category = null) }
            return
        }

        val category: RecordCategory = categorizer(
            title = s.title,
            isReminder = s.recordType == RecordType.REMINDER,
            description = s.description
        )

        _state.update { it.copy(category = category) }
    }

    // -------------------------------------------------------------------------
    // 🔥 SUGGESTIONS (AI AUTOCOMPLETE)
    // -------------------------------------------------------------------------

    private fun updateSuggestions() {
        val input = _state.value.title.trim()
        if (input.isBlank()) {
            _state.update { it.copy(suggestions = emptyList()) }
            return
        }

        val titles = recentRecords.map { it.title }
        val descriptions = recentRecords.mapNotNull { it.description }

        val domainKeywords = listOf(
            "λάδια", "service", "φρένα", "μπουζί",
            "air filter", "oil filter",
            "fuel 95", "fuel 100", "diesel",
            "τροχοί", "λάστιχα", "αντισκωριακό",
            "ΚΤΕΟ", "ασφάλεια", "τέλη κυκλοφορίας"
        )

        val request = SuggestionsRequest(
            userQuery = input,
            recentTitles = titles,
            recentDescriptions = descriptions,
            domainKeywords = domainKeywords,
            maxSuggestions = 8
        )

        val result = generateSuggestions(request)
        val texts = result.map { it.text }

        _state.update { it.copy(suggestions = texts) }
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    private fun validateSave() {
        val s = _state.value
        val valid =
            s.title.isNotBlank() &&
                    s.dateText.isNotBlank()

        _state.update { it.copy(isSaveEnabled = valid) }
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    private fun saveRecord() {
        viewModelScope.launch {
            try {
                val s = _state.value

                val reminderOdoInt: Int? =
                    s.reminderOdometer.toIntOrNull()

                val record = Record(
                    id = s.recordId ?: java.util.UUID.randomUUID().toString(),
                    vehicleId = s.vehicleId,
                    recordType = s.recordType,
                    title = s.title,
                    description = s.description.ifBlank { null },
                    date = s.date,
                    odometer = s.odometer.toIntOrNull() ?: 0,
                    cost = s.cost.toDoubleOrNull(),
                    quantity = s.quantity.toDoubleOrNull(),
                    pricePerUnit = s.pricePerUnit.toDoubleOrNull(),
                    fuelType = s.fuelTypeText.ifBlank { null },
                    isReminder = s.isReminder,
                    reminderDate = s.reminderDate,
                    // Record.reminderOdometer είναι Int, άρα:
                    reminderOdometer = reminderOdoInt ?: 0,
                    costReminder = s.costReminder.toDoubleOrNull(),
                    isCompleted = s.isCompleted
                )

                recordRepository.saveRecord(record)

                _state.update { it.copy(navigateBack = true) }

            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = "Σφάλμα κατά την αποθήκευση.")
                }
            }
        }
    }
}
