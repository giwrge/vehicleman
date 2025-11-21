package com.vehicleman.presentation.record

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.Vehicle

/**
 * UI State για τη RecordScreen.
 *
 * Περιέχει:
 * - Λίστα οχημάτων
 * - Επιλεγμένο όχημα
 * - Χρονολογική λίστα εγγραφών (expenses + reminders)
 * - Auto upcoming reminder (πρώτη μελλοντική υπενθύμιση)
 * - Scroll index για πρώτη εμφάνιση (στο "σήμερα")
 * - Κατάσταση φόρτωσης
 * - Μήνυμα λάθους
 */
data class RecordState(

    // --------------------------
    // VEHICLES
    // --------------------------
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,

    // --------------------------
    // TIMELINE (Expenses + Reminders)
    // --------------------------
    val timelineItems: List<Record> = emptyList(),

    // --------------------------
    // AUTO REMINDERS – FIRST UPCOMING
    // --------------------------
    val latestUpcomingReminder: Record? = null,

    // --------------------------
    // SCROLL POSITION
    // --------------------------
    val initialScrollIndex: Int = 0,

    // --------------------------
    // LOADING / ERROR
    // --------------------------
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
