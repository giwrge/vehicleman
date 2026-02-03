package com.vehicleman.presentation.record

import com.vehicleman.domain.model.category.RecordCategory

fun mapCategoryToDisplayName(category: RecordCategory): String {
    return when (category) {
        // Expenses
        is RecordCategory.ExpenseCategory.Fuel.FuelPurchase -> "Αγορά Καυσίμου"
        is RecordCategory.ExpenseCategory.Fuel.ElectricCharge -> "Φόρτιση"
        is RecordCategory.ExpenseCategory.Fuel.AdBlue -> "AdBlue"
        is RecordCategory.ExpenseCategory.Fuel.FuelAdditives -> "Πρόσθετα Καυσίμου"

        is RecordCategory.ExpenseCategory.Service.OilChange -> "Αλλαγή Λαδιών"
        is RecordCategory.ExpenseCategory.Service.OilFilter -> "Φίλτρο Λαδιού"
        is RecordCategory.ExpenseCategory.Service.AirFilter -> "Φίλτρο Αέρα"
        is RecordCategory.ExpenseCategory.Service.CabinFilter -> "Φίλτρο Καμπίνας"
        is RecordCategory.ExpenseCategory.Service.FuelFilter -> "Φίλτρο Καυσίμου"
        is RecordCategory.ExpenseCategory.Service.SparkPlugs -> "Μπουζί"
        is RecordCategory.ExpenseCategory.Service.Antirust -> "Αντισκωριακή Προστασία"
        is RecordCategory.ExpenseCategory.Service.TimingBelt -> "Ιμάντας Χρονισμού"
        is RecordCategory.ExpenseCategory.Service.TimingChain -> "Καδένα Χρονισμού"
        is RecordCategory.ExpenseCategory.Service.Antifreeze -> "Αντιψυκτικό"
        is RecordCategory.ExpenseCategory.Service.BrakeFluid -> "Υγρά Φρένων"
        is RecordCategory.ExpenseCategory.Service.SteeringFluid -> "Υγρά Τιμονιού"
        is RecordCategory.ExpenseCategory.Service.ValveAdjustment -> "Ρύθμιση Βαλβίδων"
        is RecordCategory.ExpenseCategory.Service.SensorsGeneral -> "Αισθητήρες (Γενικά)"
        is RecordCategory.ExpenseCategory.Service.GeneralService -> "Γενικό Service"
        is RecordCategory.ExpenseCategory.Service.SmallService -> "Μικρό Service"
        is RecordCategory.ExpenseCategory.Service.LargeService -> "Μεγάλο Service"

        is RecordCategory.ExpenseCategory.Tires.TirePurchase -> "Αγορά Ελαστικών"
        is RecordCategory.ExpenseCategory.Tires.WheelBalancing -> "Ζυγοστάθμιση"
        is RecordCategory.ExpenseCategory.Tires.WheelAlignment -> "Ευθυγράμμιση"
        is RecordCategory.ExpenseCategory.Tires.TireRepair -> "Επισκευή Ελαστικού"
        is RecordCategory.ExpenseCategory.Tires.Rims -> "Ζάντες"
        is RecordCategory.ExpenseCategory.Tires.SeasonalTireChange -> "Εποχιακή Αλλαγή Ελαστικών"

        is RecordCategory.ExpenseCategory.Repairs.Brakes -> "Φρένα"
        is RecordCategory.ExpenseCategory.Repairs.ShockAbsorbers -> "Αμορτισέρ"
        is RecordCategory.ExpenseCategory.Repairs.Battery -> "Μπαταρία"
        is RecordCategory.ExpenseCategory.Repairs.Starter -> "Μίζα"
        is RecordCategory.ExpenseCategory.Repairs.Alternator -> "Δυναμό"
        is RecordCategory.ExpenseCategory.Repairs.Clutch -> "Συμπλέκτης"
        is RecordCategory.ExpenseCategory.Repairs.Gearbox -> "Κιβώτιο Ταχυτήτων"
        is RecordCategory.ExpenseCategory.Repairs.Exhaust -> "Εξάτμιση"
        is RecordCategory.ExpenseCategory.Repairs.Radiator -> "Ψυγείο"
        is RecordCategory.ExpenseCategory.Repairs.ACService -> "Service A/C"
        is RecordCategory.ExpenseCategory.Repairs.ElectricalIssues -> "Ηλεκτρολογικά"
        is RecordCategory.ExpenseCategory.Repairs.SensorsSpecific -> "Αισθητήρες (Εξειδικευμένο)"
        is RecordCategory.ExpenseCategory.Repairs.FuelPump -> "Τρόμπα Καυσίμου"
        is RecordCategory.ExpenseCategory.Repairs.Turbo -> "Turbo"
        is RecordCategory.ExpenseCategory.Repairs.DriveShafts -> "Ημιαξόνια"
        is RecordCategory.ExpenseCategory.Repairs.SteeringRack -> "Κρεμαγιέρα"
        is RecordCategory.ExpenseCategory.Repairs.Windows -> "Παράθυρα"
        is RecordCategory.ExpenseCategory.Repairs.Multimedia -> "Multimedia / Ήχος"
        is RecordCategory.ExpenseCategory.Repairs.Wipers -> "Υαλοκαθαριστήρες"

        is RecordCategory.ExpenseCategory.Legal.Insurance -> "Ασφάλεια"
        is RecordCategory.ExpenseCategory.Legal.Tolls -> "Διόδια"
        is RecordCategory.ExpenseCategory.Legal.RoadTax -> "Τέλη Κυκλοφορίας"
        is RecordCategory.ExpenseCategory.Legal.KteoMot -> "ΚΤΕΟ"
        is RecordCategory.ExpenseCategory.Legal.LicensePlates -> "Πινακίδες"
        is RecordCategory.ExpenseCategory.Legal.Fines -> "Πρόστιμα"
        is RecordCategory.ExpenseCategory.Legal.RegistrationFees -> "Τέλη Ταξινόμησης"

        is RecordCategory.ExpenseCategory.Operational.CarWash -> "Πλύσιμο Αυτοκινήτου"
        is RecordCategory.ExpenseCategory.Operational.InteriorCleaning -> "Εσωτερικός Καθαρισμός"
        is RecordCategory.ExpenseCategory.Operational.BiologicalCleaning -> "Βιολογικός Καθαρισμός"
        is RecordCategory.ExpenseCategory.Operational.Parking -> "Parking"
        is RecordCategory.ExpenseCategory.Operational.ParkingSubscription -> "Συνδρομή Parking"
        is RecordCategory.ExpenseCategory.Operational.RoadsideAssistance -> "Οδική Βοήθεια"
        is RecordCategory.ExpenseCategory.Operational.Accessories -> "Αξεσουάρ"
        is RecordCategory.ExpenseCategory.Operational.Dashcam -> "Dashcam"
        is RecordCategory.ExpenseCategory.Operational.GpsTracker -> "GPS Tracker"
        is RecordCategory.ExpenseCategory.Operational.CleaningSupplies -> "Είδη Καθαρισμού"

        is RecordCategory.ExpenseCategory.EVSpecial.Wallbox -> "Wallbox"
        is RecordCategory.ExpenseCategory.EVSpecial.ChargerInstallation -> "Εγκατάσταση Φορτιστή"
        is RecordCategory.ExpenseCategory.EVSpecial.ChargingSubscription -> "Συνδρομή Φόρτισης"
        is RecordCategory.ExpenseCategory.EVSpecial.CablesAdapters -> "Καλώδια / Adapters"
        is RecordCategory.ExpenseCategory.EVSpecial.BatteryCoolingService -> "Service Ψύξης Μπαταρίας"

        is RecordCategory.ExpenseCategory.Damages.BodyworkPaint -> "Φανοποιείο / Βαφή"
        is RecordCategory.ExpenseCategory.Damages.WindshieldReplacement -> "Αλλαγή Παρμπρίζ"
        is RecordCategory.ExpenseCategory.Damages.WindowReplacement -> "Αλλαγή Παραθύρου"
        is RecordCategory.ExpenseCategory.Damages.BumpersPlastics -> "Προφυλακτήρες / Πλαστικά"
        is RecordCategory.ExpenseCategory.Damages.InteriorDamages -> "Εσωτερικές Ζημιές"

        // Reminders
        is RecordCategory.ReminderCategory.MaintenanceReminder.OilChangeKmDue -> "Υπενθύμιση Αλλαγής Λαδιών (χλμ)"
        is RecordCategory.ReminderCategory.MaintenanceReminder.OilChangeDateDue -> "Υπενθύμιση Αλλαγής Λαδιών (ημερ.)"
        is RecordCategory.ReminderCategory.MaintenanceReminder.TireChangeKmDue -> "Υπενθύμιση Αλλαγής Ελαστικών (χλμ)"
        is RecordCategory.ReminderCategory.MaintenanceReminder.TireChangeDateDue -> "Υπενθύμιση Αλλαγής Ελαστικών (ημερ.)"
        is RecordCategory.ReminderCategory.MaintenanceReminder.GeneralServiceDue -> "Υπενθύμιση Service"
        is RecordCategory.ReminderCategory.MaintenanceReminder.BatteryCoolingServiceDue -> "Υπενθύμιση Service Ψύξης Μπαταρίας"
        
        is RecordCategory.ReminderCategory.LegalReminder.InsuranceReminder -> "Υπενθύμιση Ασφάλειας"
        is RecordCategory.ReminderCategory.LegalReminder.RoadTaxReminder -> "Υπενθύμιση Τελών Κυκλοφορίας"
        is RecordCategory.ReminderCategory.LegalReminder.KteoReminder -> "Υπενθύμιση ΚΤΕΟ"

        is RecordCategory.ReminderCategory.GeneralReminder -> "Γενική Υπενθύμιση"

        // Fallbacks
        is RecordCategory.UnknownExpense -> "Διάφορα Έξοδα"
        is RecordCategory.UnknownReminder -> "Άγνωστη Υπενθύμιση"
    }
}
