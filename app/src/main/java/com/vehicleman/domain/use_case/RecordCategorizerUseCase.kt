package com.vehicleman.domain.use_case

import com.vehicleman.domain.model.Record
import com.vehicleman.domain.model.category.RecordCategory
import java.text.Normalizer
import java.util.Locale

/**
 * Use-case που επιστρέφει την σωστή RecordCategory (Expense ή Reminder)
 * βάση: title, isReminder, fuelType, description.
 *
 * Δεν δημιουργεί αυτόματες υπενθυμίσεις (αυτό γίνεται από άλλο use-case),
 * αλλά είναι 100% συμβατός με Manual + Auto reminders.
 */
class RecordCategorizerUseCase {

    operator fun invoke(
        title: String,
        isReminder: Boolean,
        fuelType: String? = null,
        description: String? = null
    ): RecordCategory {

        val normalized = normalize(title + " " + (description ?: ""))
        val fuelNorm = normalize(fuelType ?: "")

        // -------------- REMINDERS FIRST ---------------- //
        if (isReminder) {
            return detectReminder(normalized)
        }

        // -------------- FUEL TYPES --------------------- //
        detectFuel(fuelNorm)?.let { return it }

        // -------------- EXPENSE CATEGORIES ------------- //
        detectExpense(normalized)?.let { return it }

        // -------------- FALLBACK ------------------------ //
        return RecordCategory.UnknownExpense
    }


    // --------------------------------------------------------------------------
    //  NORMALIZATION
    // --------------------------------------------------------------------------

    private fun normalize(text: String): String {
        val n = Normalizer.normalize(text, Normalizer.Form.NFD)
        return n.replace("[\\p{InCombiningDiacriticalMarks}]+".toRegex(), "")
            .lowercase(Locale.getDefault())
    }


    // --------------------------------------------------------------------------
    //  REMINDER DETECTION (Manual reminders)
    // --------------------------------------------------------------------------

    private fun detectReminder(text: String): RecordCategory.ReminderCategory {

        // ----- Ασφάλεια -----
        if ("ασφαλ" in text || "insurance" in text)
            return RecordCategory.ReminderCategory.LegalReminder.InsuranceReminder

        // ----- Τέλη Κυκλοφορίας -----
        if ("τελη" in text || "κυκλοφορια" in text || "road tax" in text)
            return RecordCategory.ReminderCategory.LegalReminder.RoadTaxReminder

        // ----- ΚΤΕΟ / MOT -----
        if ("κτεο" in text || "mot" in text)
            return RecordCategory.ReminderCategory.LegalReminder.KteoReminder

        // ----- Λάδια / Service -----
        if ("λαδι" in text)
            return RecordCategory.ReminderCategory.MaintenanceReminder.OilChangeDateDue

        if ("service" in text || "σερβις" in text)
            return RecordCategory.ReminderCategory.MaintenanceReminder.GeneralServiceDue

        if ("ελαστικ" in text || "λαστιχ" in text)
            return RecordCategory.ReminderCategory.MaintenanceReminder.TireChangeDateDue

        // FALLBACK
        return RecordCategory.ReminderCategory.GeneralReminder
    }


    // --------------------------------------------------------------------------
    //  FUEL DETECTION (for expenses + fuel-up)
    // --------------------------------------------------------------------------

    private fun detectFuel(text: String): RecordCategory.ExpenseCategory.Fuel? {

        // ----- Electric -----
        if ("electric" in text || "ρεύμα" in text || "ρευμα" in text)
            return RecordCategory.ExpenseCategory.Fuel.ElectricCharge

        // ----- AdBlue -----
        if ("adblue" in text)
            return RecordCategory.ExpenseCategory.Fuel.AdBlue

        // ----- Additives -----
        if ("additive" in text || "προσθετα" in text)
            return RecordCategory.ExpenseCategory.Fuel.FuelAdditives

        // ----- Gasoline -----
        if (
            "95" in text || "98" in text || "100" in text ||
            "unleaded" in text || "βενζιν" in text
        ) return RecordCategory.ExpenseCategory.Fuel.FuelPurchase

        // ----- Diesel -----
        if ("diesel" in text || "b7" in text)
            return RecordCategory.ExpenseCategory.Fuel.FuelPurchase

        // ----- LPG -----
        if ("lpg" in text || "autogas" in text || "υγραεριο" in text)
            return RecordCategory.ExpenseCategory.Fuel.FuelPurchase

        // ----- CNG -----
        if ("cng" in text || "φυσικο αεριο" in text)
            return RecordCategory.ExpenseCategory.Fuel.FuelPurchase

        return null
    }


    // --------------------------------------------------------------------------
    //  EXPENSE DETECTION (Service, Tires, Repairs, Legal, Cleaning, EV, Damage)
    // --------------------------------------------------------------------------

    private fun detectExpense(text: String): RecordCategory.ExpenseCategory? {

        // ----------------- SERVICE ----------------- //
        if ("λαδι" in text) return RecordCategory.ExpenseCategory.Service.OilChange
        if ("φιλτρο λαδι" in text) return RecordCategory.ExpenseCategory.Service.OilFilter
        if ("φιλτρο αερα" in text) return RecordCategory.ExpenseCategory.Service.AirFilter
        if ("φιλτρο καμπιν" in text) return RecordCategory.ExpenseCategory.Service.CabinFilter
        if ("φιλτρο καυσ" in text) return RecordCategory.ExpenseCategory.Service.FuelFilter
        if ("μπουζ" in text) return RecordCategory.ExpenseCategory.Service.SparkPlugs
        if ("αντισκωρ" in text) return RecordCategory.ExpenseCategory.Service.Antirust
        if ("ιμαντ" in text) return RecordCategory.ExpenseCategory.Service.TimingBelt
        if ("καδενα" in text) return RecordCategory.ExpenseCategory.Service.TimingChain
        if ("αντιψυ" in text) return RecordCategory.ExpenseCategory.Service.Antifreeze
        if ("υγρα φρεν" in text) return RecordCategory.ExpenseCategory.Service.BrakeFluid
        if ("υγρα τιμον" in text) return RecordCategory.ExpenseCategory.Service.SteeringFluid
        if ("βαλβ" in text) return RecordCategory.ExpenseCategory.Service.ValveAdjustment
        if ("αισθητηρ" in text && "γεν" in text) return RecordCategory.ExpenseCategory.Service.SensorsGeneral
        if ("γενικο service" in text) return RecordCategory.ExpenseCategory.Service.GeneralService
        if ("μικρο service" in text) return RecordCategory.ExpenseCategory.Service.SmallService
        if ("μεγαλο service" in text) return RecordCategory.ExpenseCategory.Service.LargeService
        if ("service" in text) return RecordCategory.ExpenseCategory.Service.GeneralService

        // ----------------- TIRES ----------------- //
        if ("ελαστικ" in text && "αγορα" in text) return RecordCategory.ExpenseCategory.Tires.TirePurchase
        if ("ελαστικ" in text && "εποχ" in text) return RecordCategory.ExpenseCategory.Tires.SeasonalTireChange
        if ("ζυγοσταθ" in text) return RecordCategory.ExpenseCategory.Tires.WheelBalancing
        if ("ευθυγραμμ" in text) return RecordCategory.ExpenseCategory.Tires.WheelAlignment
        if ("επισκ" in text && "ελαστ" in text) return RecordCategory.ExpenseCategory.Tires.TireRepair
        if ("ζαντ" in text) return RecordCategory.ExpenseCategory.Tires.Rims

        // ----------------- REPAIRS ----------------- //
        if ("φρενα" in text || "τακακ" in text) return RecordCategory.ExpenseCategory.Repairs.Brakes
        if ("αμορτ" in text) return RecordCategory.ExpenseCategory.Repairs.ShockAbsorbers
        if ("μπαταρ" in text) return RecordCategory.ExpenseCategory.Repairs.Battery
        if ("μιζα" in text) return RecordCategory.ExpenseCategory.Repairs.Starter
        if ("δυναμ" in text) return RecordCategory.ExpenseCategory.Repairs.Alternator
        if ("συμπλ" in text) return RecordCategory.ExpenseCategory.Repairs.Clutch
        if ("κιβωτ" in text) return RecordCategory.ExpenseCategory.Repairs.Gearbox
        if ("εξατ" in text) return RecordCategory.ExpenseCategory.Repairs.Exhaust
        if ("ψυγειο" in text) return RecordCategory.ExpenseCategory.Repairs.Radiator
        if ("ac" in text || "a/c" in text) return RecordCategory.ExpenseCategory.Repairs.ACService
        if ("ηλεκτρ" in text) return RecordCategory.ExpenseCategory.Repairs.ElectricalIssues
        if ("αισθητηρ" in text) return RecordCategory.ExpenseCategory.Repairs.SensorsSpecific
        if ("τρομπ" in text) return RecordCategory.ExpenseCategory.Repairs.FuelPump
        if ("τουρμ" in text) return RecordCategory.ExpenseCategory.Repairs.Turbo
        if ("ημιαξ" in text) return RecordCategory.ExpenseCategory.Repairs.DriveShafts
        if ("κρεμαγι" in text) return RecordCategory.ExpenseCategory.Repairs.SteeringRack
        if ("παραθυρ" in text) return RecordCategory.ExpenseCategory.Repairs.Windows
        if ("multimedia" in text) return RecordCategory.ExpenseCategory.Repairs.Multimedia
        if ("υαλοκαθαριστ" in text) return RecordCategory.ExpenseCategory.Repairs.Wipers

        // ----------------- LEGAL ----------------- //
        if ("ασφαλ" in text) return RecordCategory.ExpenseCategory.Legal.Insurance
        if ("διόδια" in text || "διοδι" in text) return RecordCategory.ExpenseCategory.Legal.Tolls
        if ("τελη κυκλοφορ" in text) return RecordCategory.ExpenseCategory.Legal.RoadTax
        if ("κτεο" in text || "mot" in text) return RecordCategory.ExpenseCategory.Legal.KteoMot
        if ("πινακ" in text) return RecordCategory.ExpenseCategory.Legal.LicensePlates
        if ("προστίμ" in text || "προστιμ" in text) return RecordCategory.ExpenseCategory.Legal.Fines
        if ("ταξινομ" in text) return RecordCategory.ExpenseCategory.Legal.RegistrationFees

        // ----------------- CLEANING / OPERATIONAL ----------------- //
        if ("πλυσιμ" in text) return RecordCategory.ExpenseCategory.Operational.CarWash
        if ("εσωτερικ" in text && "καθαρ" in text) return RecordCategory.ExpenseCategory.Operational.InteriorCleaning
        if ("βιολογ" in text) return RecordCategory.ExpenseCategory.Operational.BiologicalCleaning
        if ("parking" in text) return RecordCategory.ExpenseCategory.Operational.Parking
        if ("συνδρομη parking" in text) return RecordCategory.ExpenseCategory.Operational.ParkingSubscription
        if ("δυσκολ" in text || "οδικ" in text) return RecordCategory.ExpenseCategory.Operational.RoadsideAssistance
        if ("dashcam" in text) return RecordCategory.ExpenseCategory.Operational.Dashcam
        if ("gps" in text) return RecordCategory.ExpenseCategory.Operational.GpsTracker
        if ("καθαριστικ" in text) return RecordCategory.ExpenseCategory.Operational.CleaningSupplies

        // ----------------- DAMAGE ----------------- //
        if ("φανοποι" in text || "βαφ" in text) return RecordCategory.ExpenseCategory.Damages.BodyworkPaint
        if ("παρμπριζ" in text) return RecordCategory.ExpenseCategory.Damages.WindshieldReplacement
        if ("παραθυρ" in text && "αλλαγ" in text) return RecordCategory.ExpenseCategory.Damages.WindowReplacement
        if ("προφυλακτ" in text) return RecordCategory.ExpenseCategory.Damages.BumpersPlastics
        if ("εσωτερικ" in text && "ζημι" in text) return RecordCategory.ExpenseCategory.Damages.InteriorDamages

        return null
    }
}
