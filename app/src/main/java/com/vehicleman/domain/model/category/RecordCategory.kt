package com.vehicleman.domain.model.category

/**
 * Ριζικό sealed type για όλες τις κατηγορίες εγγραφών (records):
 *  - ExpenseCategory: Όλα τα έξοδα (καύσιμα, service, λάστιχα, κτλ.)
 *  - ReminderCategory: Όλες οι υπενθυμίσεις (ασφάλεια, ΚΤΕΟ, λάδια, λάστιχα, κτλ.)
 *
 *  Χρησιμοποιείται από:
 *   - RecordCategorizerUseCase (domain)
 *   - RecordCategoryIconMapper (presentation)
 *   - UI (RecordScreen, AddEditRecordScreen) για να δείξει σωστά icons & labels
 */
sealed class RecordCategory {

    // ---------------------- EXPENSES ---------------------- //

    /**
     * Βασικός sealed τύπος για όλα τα ΕΞΟΔΑ.
     */
    sealed class ExpenseCategory : RecordCategory() {

        /**
         * ⛽ Καύσιμα / Φόρτιση
         */
        sealed class Fuel : ExpenseCategory() {
            /** ⛽ Αγορά καυσίμων */
            object FuelPurchase : Fuel()

            /** 🔌 Φόρτιση ηλεκτρικού */
            object ElectricCharge : Fuel()

            /** 🧴 AdBlue */
            object AdBlue : Fuel()

            /** 💧 Πρόσθετα καυσίμου */
            object FuelAdditives : Fuel()
        }

        /**
         * 🛠 Service / Συντήρηση
         */
        sealed class Service : ExpenseCategory() {
            /** 🔧 Αλλαγή λαδιών */
            object OilChange : Service()

            /** 🟫 Φίλτρο λαδιού */
            object OilFilter : Service()

            /** 🌬 Φίλτρο αέρα */
            object AirFilter : Service()

            /** 🫧 Φίλτρο καμπίνας */
            object CabinFilter : Service()

            /** 🟦 Φίλτρο καυσίμου */
            object FuelFilter : Service()

            /** 🔥 Μπουζί */
            object SparkPlugs : Service()

            /** 🛡 Αντισκωριακό */
            object Antirust : Service()

            /** ⏱ Ιμάντας χρονισμού */
            object TimingBelt : Service()

            /** ⛓ Καδένα χρονισμού */
            object TimingChain : Service()

            /** 🧊 Αντιψυκτικό */
            object Antifreeze : Service()

            /** 🟤 Υγρά φρένων */
            object BrakeFluid : Service()

            /** 🟪 Υγρά τιμονιού */
            object SteeringFluid : Service()

            /** 🔩 Ρυθμίσεις βαλβίδων */
            object ValveAdjustment : Service()

            /** 📡 Αισθητήρες (γενικά) */
            object SensorsGeneral : Service()

            /** 🧰 Γενικό service */
            object GeneralService : Service()

            /** 🧰 Μικρό service */
            object SmallService : Service()

            /** 🧰 Μεγάλο service */
            object LargeService : Service()
        }

        /**
         * 🛞 Ελαστικά / Τροχοί
         */
        sealed class Tires : ExpenseCategory() {
            /** 🛞 Αγορά ελαστικών */
            object TirePurchase : Tires()

            /** ⚖️ Ζυγοστάθμιση */
            object WheelBalancing : Tires()

            /** 📐 Ευθυγράμμιση */
            object WheelAlignment : Tires()

            /** 🩹 Επισκευή ελαστικού */
            object TireRepair : Tires()

            /** 🌀 Ζάντες */
            object Rims : Tires()

            /** 🔄 Αλλαγή εποχιακών ελαστικών */
            object SeasonalTireChange : Tires()
        }

        /**
         * 🧰 Επισκευές
         */
        sealed class Repairs : ExpenseCategory() {
            /** 🛑 Φρένα (τακάκια/δίσκοι) */
            object Brakes : Repairs()

            /** 🌀 Αμορτισέρ / Αναρτήσεις */
            object ShockAbsorbers : Repairs()

            /** 🔋 Μπαταρία */
            object Battery : Repairs()

            /** 🔄 Μίζα */
            object Starter : Repairs()

            /** ⚡ Δυναμό */
            object Alternator : Repairs()

            /** ⚙️ Συμπλέκτης */
            object Clutch : Repairs()

            /** 🔧 Κιβώτιο ταχυτήτων */
            object Gearbox : Repairs()

            /** 🔊 Εξάτμιση */
            object Exhaust : Repairs()

            /** 🌡 Ψυγείο */
            object Radiator : Repairs()

            /** ❄️ A/C service (επισκευή ψύξης) */
            object ACService : Repairs()

            /** 🔌 Ηλεκτρικά προβλήματα */
            object ElectricalIssues : Repairs()

            /** 📟 Αισθητήρες (O2, ABS κλπ – συγκεκριμένα) */
            object SensorsSpecific : Repairs()

            /** 💧 Τρόμπα καυσίμου */
            object FuelPump : Repairs()

            /** 🌀 Τουρμπίνα */
            object Turbo : Repairs()

            /** 🔩 Ημιαξόνια */
            object DriveShafts : Repairs()

            /** 🛞 Κρεμαγιέρα / τιμόνι */
            object SteeringRack : Repairs()

            /** 🪟 Παράθυρα / μοτέρ */
            object Windows : Repairs()

            /** 🎵 Ηχεία / multimedia */
            object Multimedia : Repairs()

            /** 🌧 Υαλοκαθαριστήρες */
            object Wipers : Repairs()
        }

        /**
         * 📑 Νομικά / Υποχρεωτικά
         */
        sealed class Legal : ExpenseCategory() {
            /** 🛡 Ασφάλεια */
            object Insurance : Legal()

            /** 🚧 Διόδια */
            object Tolls : Legal()

            /** 🧾 Τέλη κυκλοφορίας */
            object RoadTax : Legal()

            /** 📅 ΚΤΕΟ / MOT */
            object KteoMot : Legal()

            /** 🔠 Πινακίδες */
            object LicensePlates : Legal()

            /** ⚠️ Πρόστιμα */
            object Fines : Legal()

            /** 🪪 Τέλη ταξινόμησης */
            object RegistrationFees : Legal()
        }

        /**
         * 🚙 Λειτουργικά έξοδα / λειτουργία οχήματος
         */
        sealed class Operational : ExpenseCategory() {
            /** 🧼 Πλύσιμο */
            object CarWash : Operational()

            /** 🧽 Εσωτερικός καθαρισμός */
            object InteriorCleaning : Operational()

            /** 🧴 Βιολογικός καθαρισμός */
            object BiologicalCleaning : Operational()

            /** 🅿️ Parking */
            object Parking : Operational()

            /** 🅿️ Συνδρομή parking */
            object ParkingSubscription : Operational()

            /** 🚑 Οδική βοήθεια */
            object RoadsideAssistance : Operational()

            /** 🛒 Αξεσουάρ (γενικά) */
            object Accessories : Operational()

            /** 🎥 Dashcam */
            object Dashcam : Operational()

            /** 📡 GPS trackers */
            object GpsTracker : Operational()

            /** 🧹 Καθαριστικά / υλικά */
            object CleaningSupplies : Operational()
        }

        /**
         * ⚡ Εξειδικευμένα για EV
         */
        sealed class EVSpecial : ExpenseCategory() {
            /** 🔌 Wallbox */
            object Wallbox : EVSpecial()

            /** 🔧 Εγκατάσταση φορτιστή */
            object ChargerInstallation : EVSpecial()

            /** 📲 Συνδρομές φόρτισης */
            object ChargingSubscription : EVSpecial()

            /** 🔌 Καλώδια / adapters */
            object CablesAdapters : EVSpecial()

            /** 🥶 Service συστήματος ψύξης μπαταρίας */
            object BatteryCoolingService : EVSpecial()
        }

        /**
         * 💥 Ατυχήματα / Ζημιές
         */
        sealed class Damages : ExpenseCategory() {
            /** 🎨 Φανοποιία / Βαφή */
            object BodyworkPaint : Damages()

            /** 🪟 Αλλαγή παρμπρίζ */
            object WindshieldReplacement : Damages()

            /** 🪟 Αλλαγή παραθύρων */
            object WindowReplacement : Damages()

            /** 🧩 Προφυλακτήρες / πλαστικά */
            object BumpersPlastics : Damages()

            /** 🪑 Εσωτερικές ζημιές */
            object InteriorDamages : Damages()
        }
    }

    // ---------------------- REMINDERS ---------------------- //

    /**
     * Βασικός sealed τύπος για όλες τις ΥΠΕΝΘΥΜΙΣΕΙΣ.
     *
     * Χρησιμοποιείται για:
     *  - αυτόματα reminders από Vehicle (λάδια, ελαστικά, ασφάλεια, τέλη, ΚΤΕΟ)
     *  - χειροκίνητα reminders του χρήστη
     */
    sealed class ReminderCategory : RecordCategory() {

        /**
         * Υπενθυμίσεις Συντήρησης / Service
         */
        sealed class MaintenanceReminder : ReminderCategory() {
            /** Reminder αλλαγής λαδιών ανά km */
            object OilChangeKmDue : MaintenanceReminder()

            /** Reminder αλλαγής λαδιών ανά ημερομηνία */
            object OilChangeDateDue : MaintenanceReminder()

            /** Reminder αλλαγής ελαστικών ανά km */
            object TireChangeKmDue : MaintenanceReminder()

            /** Reminder αλλαγής ελαστικών ανά ημερομηνία */
            object TireChangeDateDue : MaintenanceReminder()

            /** Reminder γενικού service */
            object GeneralServiceDue : MaintenanceReminder()

            /** Reminder για σύστημα ψύξης μπαταρίας σε EV */
            object BatteryCoolingServiceDue : MaintenanceReminder()
        }

        /**
         * Νομικές / Υποχρεωτικές Υπενθυμίσεις
         */
        sealed class LegalReminder : ReminderCategory() {
            /** Υπενθύμιση Ασφάλειας */
            object InsuranceReminder : LegalReminder()

            /** Υπενθύμιση Τελών Κυκλοφορίας */
            object RoadTaxReminder : LegalReminder()

            /** Υπενθύμιση ΚΤΕΟ / MOT */
            object KteoReminder : LegalReminder()
        }

        /**
         * Γενικές υπενθυμίσεις χρήστη (κάτι άλλο που θέλει να θυμηθεί)
         */
        object GeneralReminder : ReminderCategory()
    }

    // ---------------------- FALLBACKS ---------------------- //

    /**
     * Χρησιμοποιείται όταν δεν μπορέσαμε να χαρτογραφήσουμε κάποιο έξοδο
     * σε πιο συγκεκριμένη κατηγορία (όχι υπενθύμιση).
     */
    object UnknownExpense : RecordCategory()

    /**
     * Χρησιμοποιείται όταν δεν μπορέσαμε να χαρτογραφήσουμε κάποια υπενθύμιση
     * σε πιο συγκεκριμένη reminder κατηγορία.
     */
    object UnknownReminder : RecordCategory()
}
