package com.vehicleman.presentation.addeditrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.RecordExpenseCategory
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.TranslateTitlePreference
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.GetLastFuelUpRecord
import com.vehicleman.domain.use_case.recordcategorizer.RecordCategorizerUseCase
import com.vehicleman.domain.use_case.recordcategorizer.RecordSynonymDictionary
import com.vehicleman.domain.use_case.record_ai.*
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.presentation.record.mapCategoryToDisplayName
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val categorizer: RecordCategorizerUseCase,
    private val parseSmartTitle: ParseSmartTitleUseCase,
    private val predictRecordType: PredictRecordTypeFromParsedDataUseCase,
    private val autoFillFuel: AutoFillFuelDataUseCase,
    private val autoFillExpense: AutoFillExpenseDataUseCase,
    private val autoFillReminder: AutoFillReminderDataUseCase,
    private val generateSuggestions: GenerateSmartSuggestionsUseCase,
    private val getLastFuelUpRecord: GetLastFuelUpRecord,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditRecordState())
    val state = _state.asStateFlow()

    private val vehicleId: String = savedStateHandle.get<String>(NavDestinations.VEHICLE_ID_KEY) ?: ""
    private val recordId: String? = savedStateHandle.get<String>(NavDestinations.RECORD_ID_KEY)
    private val fromReminder: Boolean = savedStateHandle.get<Boolean>(NavDestinations.FROM_REMINDER_KEY) ?: false

    private var originalReminder: Record? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var lastFuelOdometerKm: Int? = null
    private var recentRecords: List<Record> = emptyList()

    init {
        if (vehicleId.isBlank()) {
            _state.update { it.copy(isLoading = false, errorMessage = "Λείπει το όχημα.") }
        } else {
            _state.update { it.copy(vehicleId = vehicleId) }
            viewModelScope.launch {
                val vehicle = vehicleRepository.getVehicleById(vehicleId)
                val fuelTypes = vehicle?.fuelTypes ?: emptyList()
                
                // Get the very last odometer from this vehicle
                val lastOdometer = recordRepository.getLatestOdometer(vehicleId) ?: vehicle?.currentOdometer ?: 0
                
                recentRecords = recordRepository.getRecordsByVehicle(vehicleId)
                
                _state.update { 
                    it.copy(
                        vehicleFuelTypes = fuelTypes,
                        odometer = if (it.isNew) lastOdometer.toString() else it.odometer
                    ) 
                }
            }
            if (fromReminder && recordId != null) convertReminderToExpense(recordId)
            else if (recordId == null || recordId == "new") loadNewRecord()
            else loadExistingRecord(recordId)
        }
    }

    private fun convertReminderToExpense(reminderId: String) {
        viewModelScope.launch {
            val reminder = recordRepository.getRecordById(reminderId)
            if (reminder == null || !reminder.isReminder) {
                _state.update { it.copy(errorMessage = "Δεν βρέθηκε η υπενθύμιση.") }
                return@launch
            }
            originalReminder = reminder
            _state.update {
                it.copy(
                    isLoading = false,
                    isNew = true,
                    recordType = RecordType.EXPENSE,
                    title = reminder.title,
                    description = reminder.description ?: "",
                    date = Date(),
                    dateText = dateFormat.format(Date()),
                    odometer = reminder.reminderOdometer?.toString() ?: "",
                    cost = reminder.costReminder?.toString() ?: "",
                    isReminder = false,
                    showReminderFields = false
                )
            }
        }
    }

    private fun loadNewRecord() {
        val now = Date()
        _state.update { 
            it.copy(
                isLoading = false, 
                isNew = true, 
                date = now, 
                dateText = dateFormat.format(now),
                titleFieldValue = TextFieldValue("")
            ) 
        }
    }

    private fun loadExistingRecord(id: String) {
        viewModelScope.launch {
            recordRepository.getRecordById(id)?.let { record ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isNew = false,
                        recordId = record.id,
                        recordType = record.recordType,
                        title = record.title,
                        titleFieldValue = TextFieldValue(record.title),
                        description = record.description ?: "",
                        date = record.date,
                        dateText = dateFormat.format(record.date),
                        odometer = record.odometer.toString(),
                        cost = record.cost?.toString() ?: "",
                        quantity = record.quantity?.toString() ?: "",
                        pricePerUnit = record.pricePerUnit?.toString() ?: "",
                        fuelTypeText = record.fuelType ?: "",
                        isReminder = record.isReminder,
                        isCompleted = record.isCompleted,
                        reminderDate = record.reminderDate,
                        reminderDateText = record.reminderDate?.let { d -> dateFormat.format(d) } ?: "",
                        reminderOdometer = record.reminderOdometer?.toString() ?: "",
                        costReminder = record.costReminder?.toString() ?: "",
                        showReminderFields = record.isReminder
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditRecordEvent) {
        when (event) {
            is AddEditRecordEvent.TitleChanged -> {
                _state.update { it.copy(title = event.value, titleFieldValue = TextFieldValue(event.value, selection = TextRange(event.value.length))) }
                updateSuggestions(event.value)
                validateSave()
            }
            is AddEditRecordEvent.TitleFieldValueChanged -> {
                _state.update { it.copy(title = event.value.text, titleFieldValue = event.value) }
                updateSuggestions(event.value.text)
                validateSave()
            }
            AddEditRecordEvent.TitleFocusLost -> {
                processIntelligentTitle(_state.value.title, forceApplyTranslation = true)
            }
            is AddEditRecordEvent.DescriptionChanged -> _state.update { it.copy(description = event.value) }
            is AddEditRecordEvent.OdometerChanged -> _state.update { it.copy(odometer = event.value) }
            is AddEditRecordEvent.CostChanged -> {
                _state.update { it.copy(cost = event.value) }
                triggerFuelAutofill()
            }
            is AddEditRecordEvent.QuantityChanged -> {
                _state.update { it.copy(quantity = event.value) }
                triggerFuelAutofill()
            }
            is AddEditRecordEvent.PricePerUnitChanged -> {
                _state.update { it.copy(pricePerUnit = event.value) }
                triggerFuelAutofill()
            }
            is AddEditRecordEvent.FuelTypeChanged -> _state.update { it.copy(fuelTypeText = event.value) }
            AddEditRecordEvent.ToggleFullTank -> _state.update { it.copy(isFullTank = !it.isFullTank) }
            is AddEditRecordEvent.DateChanged -> {
                val isFuture = event.value.after(Date())
                _state.update { 
                    it.copy(
                        date = event.value, 
                        dateText = dateFormat.format(event.value),
                        isFutureDate = isFuture,
                        recordType = if (isFuture) RecordType.REMINDER else {
                            if (it.recordType == RecordType.REMINDER) RecordType.EXPENSE else it.recordType
                        },
                        showReminderFields = isFuture
                    ) 
                }
                validateSave()
            }
            is AddEditRecordEvent.RecordTypeChanged -> {
                _state.update { it.copy(recordType = event.value) }
            }
            is AddEditRecordEvent.SuggestionClicked -> {
                applySuggestion(event.item)
            }
            is AddEditRecordEvent.ToggleCompleted -> {
                _state.update { it.copy(isCompleted = !it.isCompleted) }
            }
            is AddEditRecordEvent.CostReminderChanged -> _state.update { it.copy(costReminder = event.value) }
            is AddEditRecordEvent.ReminderOdometerChanged -> _state.update { it.copy(reminderOdometer = event.value) }
            AddEditRecordEvent.Save -> onSaveClicked()
            AddEditRecordEvent.ErrorShown -> _state.update { it.copy(errorMessage = null) }
            AddEditRecordEvent.NavigateBackConsumed -> _state.update { it.copy(navigateBack = false) }
            else -> {}
        }
    }

    private fun processIntelligentTitle(newTitle: String, forceApplyTranslation: Boolean = false) {
        if (newTitle.isBlank()) return
        val nowLocal = LocalDate.now()
        val parsed = parseSmartTitle(newTitle, nowLocal)
        
        // 1. Determine Type & Date
        val isFuture = parsed.isFutureDate
        val typeHint = predictRecordType(parsed)
        val mappedType = when {
            isFuture -> RecordType.REMINDER
            typeHint == RecordTypeHint.FUEL -> RecordType.FUEL_UP
            else -> RecordType.EXPENSE
        }

        val detectedDate = parsed.detectedDate?.let {
            Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        // 2. Auto-fill Data
        val translatedTitle = mapCategoryToDisplayName(parsed.detectedCategory)
        
        _state.update { s ->
            val finalDate = detectedDate ?: s.date
            val finalTitle = if (forceApplyTranslation) translatedTitle else s.title
            s.copy(
                recordType = mappedType,
                category = parsed.detectedCategory,
                title = finalTitle,
                titleFieldValue = if (forceApplyTranslation) TextFieldValue(finalTitle, selection = TextRange(finalTitle.length)) else s.titleFieldValue,
                date = finalDate,
                dateText = dateFormat.format(finalDate),
                isFutureDate = isFuture,
                showReminderFields = isFuture,
                cost = parsed.detectedCostEuro?.toString() ?: s.cost,
                quantity = parsed.detectedLiters?.toString() ?: s.quantity,
                pricePerUnit = parsed.detectedPricePerLiter?.toString() ?: s.pricePerUnit,
                fuelTypeText = parsed.detectedFuelType?.let { mapFuelTypeHint(it) } ?: s.fuelTypeText,
                description = if (s.description.isBlank()) generateAutoDescription(parsed, finalDate) else s.description
            )
        }
        
        if (mappedType == RecordType.FUEL_UP) triggerFuelAutofill()
    }

    private fun mapFuelTypeHint(hint: FuelTypeHint): String {
        return when (hint) {
            FuelTypeHint.UNLEADED_95 -> "unleaded_95"
            FuelTypeHint.UNLEADED_100 -> "unleaded_100"
            FuelTypeHint.DIESEL -> "diesel"
            FuelTypeHint.LPG -> "lpg"
            FuelTypeHint.CNG -> "cng"
            FuelTypeHint.ELECTRIC -> "electric"
            else -> ""
        }
    }

    private fun generateAutoDescription(parsed: ParsedSmartTitle, date: Date): String {
        val expenseResult = autoFillExpense(parsed)
        val dateStr = dateFormat.format(date)
        val baseDesc = expenseResult.autoDescription
        
        return if (baseDesc.isNotBlank()) "$baseDesc - $dateStr" else dateStr
    }

    private fun triggerFuelAutofill() {
        val s = _state.value
        if (s.recordType != RecordType.FUEL_UP) return

        val request = FuelAutofillRequest(
            costEuro = s.cost.toDoubleOrNull(),
            liters = s.quantity.toDoubleOrNull(),
            pricePerLiter = s.pricePerUnit.toDoubleOrNull(),
            fuelTypeHint = null
        )
        
        val result = autoFillFuel(request)
        
        _state.update { 
            it.copy(
                cost = result.costEuro?.toString() ?: it.cost,
                quantity = result.liters?.toString() ?: it.quantity,
                pricePerUnit = result.pricePerLiter?.toString() ?: it.pricePerUnit
            )
        }
    }

    private fun updateSuggestions(query: String) {
        viewModelScope.launch {
            val request = SuggestionsRequest(
                userQuery = query,
                recentRecords = recentRecords.map { 
                    RecentRecordSuggestion(it.id, it.title, it.description, it.date) 
                },
                domainKeywords = RecordSynonymDictionary.allKeywords()
            )
            val suggestions = generateSuggestions(request)
            _state.update { it.copy(suggestions = suggestions) }
        }
    }

    private fun applySuggestion(item: SuggestionItem) {
        if (item.source == SuggestionSource.RECENT_RECORD && item.recordId != null) {
            viewModelScope.launch {
                val record = recordRepository.getRecordById(item.recordId)
                if (record != null) {
                    _state.update { s ->
                        s.copy(
                            title = record.title,
                            titleFieldValue = TextFieldValue(record.title, selection = TextRange(record.title.length)),
                            description = record.description ?: "",
                            cost = record.cost?.toString() ?: "",
                            quantity = record.quantity?.toString() ?: "",
                            pricePerUnit = record.pricePerUnit?.toString() ?: "",
                            fuelTypeText = record.fuelType ?: "",
                            isFullTank = record.isFullTank,
                            recordType = record.recordType,
                            category = mapToRecordCategory(record)
                        )
                    }
                }
            }
        } else {
            _state.update { it.copy(title = item.text, titleFieldValue = TextFieldValue(item.text, selection = TextRange(item.text.length))) }
            processIntelligentTitle(item.text, forceApplyTranslation = true)
        }
    }

    private fun mapToRecordCategory(record: Record): RecordCategory? {
        // Implement logic to map back from RecordExpenseCategory to RecordCategory if possible,
        // or just rely on processIntelligentTitle to re-detect it.
        return null
    }

    private fun validateSave() {
        val s = _state.value
        _state.update { it.copy(isSaveEnabled = s.title.isNotBlank()) }
    }

    private fun onSaveClicked() {
        saveRecord()
    }

    private fun saveRecord() {
        viewModelScope.launch {
            val s = _state.value
            val record = Record(
                id = s.recordId ?: java.util.UUID.randomUUID().toString(),
                vehicleId = s.vehicleId,
                recordType = s.recordType,
                category = mapToEnumCategory(s.category),
                title = s.title,
                description = s.description.ifBlank { null },
                date = s.date,
                odometer = s.odometer.toIntOrNull() ?: 0,
                cost = s.cost.toDoubleOrNull(),
                quantity = s.quantity.toDoubleOrNull(),
                pricePerUnit = s.pricePerUnit.toDoubleOrNull(),
                fuelType = s.fuelTypeText.ifBlank { null },
                isFullTank = s.isFullTank, // ✅ NEW
                isReminder = s.isReminder,
                reminderDate = s.reminderDate,
                reminderOdometer = s.reminderOdometer.toIntOrNull() ?: 0,
                costReminder = s.costReminder.toDoubleOrNull(),
                isCompleted = s.isCompleted
            )
            recordRepository.saveRecord(record)
            _state.update { it.copy(navigateBack = true) }
        }
    }

    private fun mapToEnumCategory(category: RecordCategory?): RecordExpenseCategory? {
        return when (category) {
            is RecordCategory.ExpenseCategory.Fuel -> RecordExpenseCategory.FUEL
            is RecordCategory.ExpenseCategory.Service -> RecordExpenseCategory.SERVICE
            is RecordCategory.ExpenseCategory.Tires -> RecordExpenseCategory.TIRES
            is RecordCategory.ExpenseCategory.Repairs -> RecordExpenseCategory.REPAIRS
            is RecordCategory.ExpenseCategory.Legal -> {
                when (category) {
                    is RecordCategory.ExpenseCategory.Legal.Insurance -> RecordExpenseCategory.INSURANCE
                    is RecordCategory.ExpenseCategory.Legal.RoadTax -> RecordExpenseCategory.TAXES
                    else -> RecordExpenseCategory.TAXES
                }
            }
            is RecordCategory.ExpenseCategory.Operational -> {
                when (category) {
                    is RecordCategory.ExpenseCategory.Operational.CarWash,
                    is RecordCategory.ExpenseCategory.Operational.InteriorCleaning,
                    is RecordCategory.ExpenseCategory.Operational.BiologicalCleaning -> RecordExpenseCategory.CLEANING
                    is RecordCategory.ExpenseCategory.Operational.Parking,
                    is RecordCategory.ExpenseCategory.Operational.ParkingSubscription -> RecordExpenseCategory.PARKING
                    else -> RecordExpenseCategory.OTHER
                }
            }
            is RecordCategory.ExpenseCategory.EVSpecial -> RecordExpenseCategory.SERVICE
            is RecordCategory.ExpenseCategory.Damages -> RecordExpenseCategory.REPAIRS
            else -> RecordExpenseCategory.OTHER
        }
    }
}
