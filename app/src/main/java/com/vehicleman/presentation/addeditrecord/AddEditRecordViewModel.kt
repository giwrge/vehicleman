package com.vehicleman.presentation.addeditrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.GetLastFuelUpRecord
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillExpenseDataUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillFuelDataUseCase
import com.vehicleman.domain.use_case.record_ai.AutoFillReminderDataUseCase
import com.vehicleman.domain.use_case.record_ai.ExpenseAutofillResult
import com.vehicleman.domain.use_case.record_ai.FuelAutofillRequest
import com.vehicleman.domain.use_case.record_ai.FuelTypeHint
import com.vehicleman.domain.use_case.record_ai.GenerateSmartSuggestionsUseCase
import com.vehicleman.domain.use_case.record_ai.ParseSmartTitleUseCase
import com.vehicleman.domain.use_case.record_ai.ParsedSmartTitle
import com.vehicleman.domain.use_case.record_ai.PredictRecordTypeFromParsedDataUseCase
import com.vehicleman.domain.use_case.record_ai.RecordTypeHint
import com.vehicleman.domain.use_case.record_ai.ReminderAutofillRequest
import com.vehicleman.domain.use_case.record_ai.ReminderAutofillResult
import com.vehicleman.domain.use_case.record_ai.SuggestionsRequest
import com.vehicleman.domain.use_case.record_ai.RecentRecordSuggestion
import com.vehicleman.domain.use_case.record_ai.cleanedText
import com.vehicleman.domain.use_case.recordcategorizer.RecordSynonymDictionary
import com.vehicleman.domain.use_case.recordcategorizer.RecordSynonymNormalizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val vehicleRepository: VehicleRepository, // (δεν χρησιμοποιείται ακόμα, το κρατάμε)
    private val categorizer: RecordCategorizerUseCase,

    // 🔥 AI use cases
    private val parseSmartTitle: ParseSmartTitleUseCase,
    private val predictRecordType: PredictRecordTypeFromParsedDataUseCase,
    private val autoFillFuel: AutoFillFuelDataUseCase,
    private val autoFillExpense: AutoFillExpenseDataUseCase,
    private val autoFillReminder: AutoFillReminderDataUseCase,
    private val generateSuggestions: GenerateSmartSuggestionsUseCase,

    // 🔥 Odometer helpers
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
    private var lastFuelOdometerKm: Int? = null
    private var recentRecords: List<Record> = emptyList()

    init {
        if (vehicleId.isBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Λείπει το όχημα για την εγγραφή."
                )
            }
        } else {
            _state.update { it.copy(vehicleId = vehicleId) }

            // 🔥 Φόρτωση last FUEL odometer + recent records
            viewModelScope.launch {
                val lastFuelRecord = getLastFuelUpRecord(vehicleId)
                lastFuelOdometerKm = lastFuelRecord?.odometer
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
                    costReminder = record.costReminder?.toString() ?: "",
                    isReminderSwitchLocked = record.isReminder,
                    showReminderFields = record.isReminder,
                    category = null,
                    suggestions = emptyList()
                )
            }

            updateCategorization()
        }
    }

    // -------------------------------------------------------------------------
    // INTELLIGENT DATE HANDLING (future date ⇒ reminder)
    // -------------------------------------------------------------------------
    private fun processIntelligentDate(newDate: Date) {
        val todayLocal = LocalDate.now()
        val newLocal = newDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        if (!newLocal.isAfter(todayLocal)) return

        val title = _state.value.title
        val parsed = parseSmartTitle(title, todayLocal)

        val req = ReminderAutofillRequest(
            lastOdometerKm = lastFuelOdometerKm,
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
                costReminder = if (old.costReminder.isBlank() && result.costEuro != null)
                    result.costEuro.toString() else old.costReminder,
                description = if (old.description.isBlank() && result.autoDescription.isNotBlank())
                    result.autoDescription else old.description
            )
        }

        updateCategorization()
    }

    // -------------------------------------------------------------------------
    // EVENT HANDLING
    // -------------------------------------------------------------------------
    fun onEvent(event: AddEditRecordEvent) {
        when (event) {
            is AddEditRecordEvent.LoadExisting -> loadExistingRecord(event.recordId)

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

            is AddEditRecordEvent.RecordTypeChanged -> handleRecordTypeChange(event.value)

            is AddEditRecordEvent.SuggestionClicked -> {
                applySuggestion(event.item)
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

            AddEditRecordEvent.ErrorShown -> _state.update { it.copy(errorMessage = null) }

            AddEditRecordEvent.NavigateBackConsumed -> _state.update { it.copy(navigateBack = false) }
        }
    }

    // -------------------------------------------------------------------------
    // 🔥 AI PIPELINE ΓΙΑ ΤΟΝ ΤΙΤΛΟ
    // -------------------------------------------------------------------------
    private fun processIntelligentTitle(newTitle: String) {
        val s = _state.value
        if (newTitle.isBlank()) {
            _state.update { it.copy(suggestions = emptyList(), category = null) }
            return
        }

        val nowLocal = s.date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val parsed = parseSmartTitle(newTitle, nowLocal)
        val typeHint = predictRecordType(parsed)

        // Για να μην πετάει reminder όταν δεν υπάρχει future date
        val mappedType = when (typeHint) {
            RecordTypeHint.FUEL -> RecordType.FUEL_UP
            RecordTypeHint.REMINDER -> RecordType.EXPENSE
            RecordTypeHint.EXPENSE, RecordTypeHint.UNKNOWN -> RecordType.EXPENSE
        }

        if (mappedType != s.recordType) handleRecordTypeChange(mappedType)

        when (mappedType) {
            RecordType.FUEL_UP -> applyFuelAutofill(parsed)
            RecordType.EXPENSE -> applyExpenseAutofill(parsed)
            RecordType.REMINDER -> applyReminderAutofill(parsed, nowLocal)
        }

        updateSuggestions()
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

        val kmSinceLastFill = lastFuelOdometerKm?.let { lastFuelKm ->
            val currentKm = _state.value.odometer.toIntOrNull()
            if (currentKm != null && currentKm > lastFuelKm) currentKm - lastFuelKm else null
        }

        val autoDescription = buildString {
            append("Γέμισμα καυσίμου")
            if (result.fuelTypeHint != null) append(" (${mapFuelTypeHintToString(result.fuelTypeHint)})")
            if (result.liters != null && result.pricePerLiter != null) {
                append(" ${"%.2f".format(result.liters)} lt × ${result.pricePerLiter} €/lt")
            }
            if (result.costEuro != null) append(" = ${result.costEuro}€")
            if (kmSinceLastFill != null) append(" — Διαδρομή από το προηγούμενο γέμισμα: $kmSinceLastFill km")
        }

        _state.update { old ->
            old.copy(
                cost = result.costEuro?.toString() ?: old.cost,
                quantity = result.liters?.toString() ?: old.quantity,
                pricePerUnit = result.pricePerLiter?.toString() ?: old.pricePerUnit,
                fuelTypeText = result.fuelTypeHint?.let { mapFuelTypeHintToString(it) } ?: old.fuelTypeText,
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
            if (result.costEuro != null) append(" — Κόστος: ${result.costEuro}€")
            append(" — Ημ/νία: $dateText")
        }

        _state.update { old ->
            old.copy(
                cost = result.costEuro?.toString() ?: old.cost,
                description = autoDescription
            )
        }
    }

    private fun applyReminderAutofill(parsed: ParsedSmartTitle, nowLocal: LocalDate) {
        val req = ReminderAutofillRequest(
            lastOdometerKm = lastFuelOdometerKm,
            fallbackDate = nowLocal
        )

        val result = autoFillReminder(parsed, req)

        val reminderDate = result.reminderDate?.let { local ->
            Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant())
        } ?: Date.from(nowLocal.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val autoDescription = buildString {
            append("Υπενθύμιση: ")
            append(parsed.cleanedText)
            if (result.autoDescription.isNotBlank()) {
                append(" — ")
                append(result.autoDescription)
            }
            append(" στις ${dateFormat.format(reminderDate)}")
            if (result.reminderKm != null) append(" — στα ${result.reminderKm} km")
        }

        _state.update { old ->
            old.copy(
                reminderDate = reminderDate,
                reminderDateText = dateFormat.format(reminderDate),
                reminderOdometer = result.reminderKm?.toString() ?: old.reminderOdometer,
                costReminder = result.costEuro?.toString() ?: old.costReminder,
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
    private fun applySuggestion(item: com.vehicleman.domain.use_case.record_ai.SuggestionItem) {
        viewModelScope.launch {
            val recordId = item.recordId

            if (recordId != null) {
                // ✅ Fill EVERYTHING from existing record
                val record = recordRepository.getRecordById(recordId) ?: run {
                    // fallback: just put the text
                    _state.update { it.copy(title = item.text) }
                    processIntelligentTitle(item.text)
                    return@launch
                }

                _state.update { s ->
                    s.copy(
                        // keep current vehicleId / recordId (we are creating a new record normally)
                        title = record.title,
                        description = record.description.orEmpty(),

                        recordType = record.recordType,
                        date = record.date,
                        dateText = dateFormat.format(record.date),

                        odometer = record.odometer.toString(),
                        cost = record.cost?.toString().orEmpty(),
                        quantity = record.quantity?.toString().orEmpty(),
                        pricePerUnit = record.pricePerUnit?.toString().orEmpty(),
                        fuelType = record.fuelType,
                        fuelTypeText = record.fuelType.orEmpty(),

                        isReminder = record.isReminder,
                        isCompleted = record.isCompleted,
                        reminderDate = record.reminderDate,
                        reminderDateText = record.reminderDate?.let { d -> dateFormat.format(d) }.orEmpty(),
                        reminderOdometer = record.reminderOdometer?.toString().orEmpty(),
                        costReminder = record.costReminder?.toString().orEmpty(),

                        showCostDetails = record.recordType == RecordType.FUEL_UP,
                        showFuelTypeSelection = record.recordType == RecordType.FUEL_UP,
                        showReminderFields = record.recordType == RecordType.REMINDER || record.isReminder,
                        isReminderSwitchLocked = false,

                        suggestions = emptyList()
                    )
                }

                updateCategorization()
                return@launch
            }

            // Keyword/system suggestion: use as title (or append if you prefer)
            _state.update { it.copy(title = item.text) }
            processIntelligentTitle(item.text)
        }
    }

    private fun updateSuggestions() {
        val inputRaw = _state.value.title.trim()
        if (inputRaw.isBlank()) {
            _state.update { it.copy(suggestions = emptyList()) }
            return
        }

        val recent = recentRecords.map {
            RecentRecordSuggestion(
                recordId = it.id,
                title = it.title,
                description = it.description,
                date = it.date
            )
        }

        val request = SuggestionsRequest(
            userQuery = inputRaw,
            recentRecords = recent,
            domainKeywords = RecordSynonymDictionary.allKeywords(),
            maxSuggestions = 8
        )

        val result = generateSuggestions(request)

        _state.update { it.copy(suggestions = result) } // ✅ όχι map{it.text}
    }




    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------
    private fun validateSave() {
        val s = _state.value
        val valid = s.title.isNotBlank() && s.dateText.isNotBlank()
        _state.update { it.copy(isSaveEnabled = valid) }
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------
    private fun saveRecord() {
        viewModelScope.launch {
            try {
                val s = _state.value
                val reminderOdoInt = s.reminderOdometer.toIntOrNull()

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
                    reminderOdometer = reminderOdoInt ?: 0,
                    costReminder = s.costReminder.toDoubleOrNull(),
                    isCompleted = s.isCompleted
                )

                recordRepository.saveRecord(record)
                _state.update { it.copy(navigateBack = true) }

            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Σφάλμα κατά την αποθήκευση.") }
            }
        }
    }
}
