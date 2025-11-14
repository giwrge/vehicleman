package com.vehicleman.presentation.record

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle

/**
 * Expanded, UI-friendly state for the Record screen.
 * - expenseRecords: descending date (most recent first)
 * - reminderRecords: ascending reminderDate (next first)
 * - latestUpcomingReminder: the nearest future reminder (sticky)
 */
data class RecordState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,

    // full timeline (mixed) if needed
    val timelineItems: List<Record> = emptyList(),

    // split lists for UI convenience
    val expenseRecords: List<Record> = emptyList(),         // sorted DESC by date
    val reminderRecords: List<Record> = emptyList(),        // sorted ASC by reminderDate
    val latestUpcomingReminder: Record? = null,             // the sticky top item (closest future reminder)

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
