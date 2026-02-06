package com.vehicleman.presentation.addeditrecord

import com.vehicleman.domain.model.RecordType
import com.vehicleman.domain.model.category.RecordCategory
import com.vehicleman.domain.use_case.record_ai.SuggestionItem
import java.util.Date

data class AddEditRecordState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val navigateBack: Boolean = false,
    val errorMessage: String? = null,

    val vehicleId: String = "",
    val recordId: String? = null,

    val recordType: RecordType = RecordType.EXPENSE,
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val dateText: String = "",
    val isFutureDate: Boolean = false, // <-- ΝΕΑ ΜΕΤΑΒΛΗΤΗ

    val odometer: String = "",
    val cost: String = "",
    val quantity: String = "",
    val pricePerUnit: String = "",

    val fuelType: String? = null,
    val fuelTypeText: String = "",

    val isReminder: Boolean = false,
    val isCompleted: Boolean = false,
    val reminderDate: Date? = null,
    val reminderDateText: String = "",
    val reminderOdometer: String = "",
    val costReminder: String = "",          // ✅ NEW (string for UI)
    val isReminderSwitchLocked: Boolean = false,
    val showReminderFields: Boolean = false,

    val showCostDetails: Boolean = false,
    val showFuelTypeSelection: Boolean = false,

    val suggestions: List<SuggestionItem> = emptyList(),   // ✅ CHANGED
    val suggestionClicked: Boolean = false,
    val showTranslateTitleDialog: Boolean = false,

    val vehicleFuelTypes: List<String> = emptyList(),
    val selectedFuelType: String? = null,

    val category: RecordCategory? = null,

    val isSaveEnabled: Boolean = false
)
