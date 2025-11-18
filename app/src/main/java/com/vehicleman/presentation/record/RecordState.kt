package com.vehicleman.presentation.record

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle

/**
 * Expanded, UI-friendly state for the Record screen.
 * - timelineItems: A single, chronologically sorted list of all records.
 * - latestUpcomingReminder: the nearest future reminder (for the sticky header).
 * - initialScrollIndex: The suggested index to scroll to upon first load or after a timeout.
 */
data class RecordState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    val timelineItems: List<Record> = emptyList(), // Single, sorted list for the UI
    val latestUpcomingReminder: Record? = null,   // The sticky top item (closest future reminder)
    val initialScrollIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
