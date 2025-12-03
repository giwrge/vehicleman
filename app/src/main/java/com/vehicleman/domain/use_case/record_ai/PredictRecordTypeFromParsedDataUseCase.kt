package com.vehicleman.domain.use_case.record_ai

import com.vehicleman.domain.model.RecordType
import javax.inject.Inject

class PredictRecordTypeFromParsedDataUseCase @Inject constructor() {

    /**
     * Παίρνουμε το ParsedInput και αποφασίζουμε:
     *  - EXPENSE (service, επισκευές, γενικά έξοδα)
     *  - FUEL_UP (όταν αφορά καύσιμα ή μοιάζει με ανεφοδιασμό)
     *  - REMINDER (όταν αφορά μελλοντική ημερομηνία ή keywords)
     */
    operator fun invoke(input: ParsedInput): RecordType {

        // -----------------------------------------------------
        // 1) ΑΝ ΥΠΑΡΧΕΙ ΜΕΛΛΟΝΤΙΚΗ ΗΜΕΡΟΜΗΝΙΑ → είναι REMINDER
        // -----------------------------------------------------
        if (input.containsFutureDate) {
            return RecordType.REMINDER
        }

        // -----------------------------------------------------
        // 2) Αν βρήκαμε keywords υπενθύμισης στο title
        // -----------------------------------------------------
        if (input.reminderKeywords) {
            return RecordType.REMINDER
        }

        // -----------------------------------------------------
        // 3) Αν αναγνωρίστηκαν keywords καυσίμων → FUEL_UP
        // -----------------------------------------------------
        if (input.fuelKeywords) {
            return RecordType.FUEL_UP
        }

        // -----------------------------------------------------
        // 4) Fuel-like pattern ("benzini 50e 1.78", "5lt diesel")
        // -----------------------------------------------------
        if (input.detectedLiters != null ||
            input.detectedPricePerLiter != null ||
            input.detectedFuelType != null
        ) {
            return RecordType.FUEL_UP
        }

        // -----------------------------------------------------
        // 5) Αν υπάρχουν service keywords → EXPENSE
        // -----------------------------------------------------
        if (input.serviceKeywords) {
            return RecordType.EXPENSE
        }

        // -----------------------------------------------------
        // 6) Αν ΔΕΝ υπάρχουν keywords → fallback:
        //    Αν υπάρχει past date → EXPENSE
        //    Αν δεν υπάρχει τίποτα αναγνωρίσιμο → EXPENSE (default)
        // -----------------------------------------------------
        return RecordType.EXPENSE
    }
}
