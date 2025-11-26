package com.vehicleman.presentation.record

import com.vehicleman.R
import com.vehicleman.domain.model.category.RecordCategory

/**
 * Χαρτογραφεί κάθε RecordCategory σε ένα icon drawable.
 * Υποστηρίζει ΟΛΕΣ τις νέες subcategories του enterprise sealed tree.
 */

fun mapCategoryToIcon(category: RecordCategory): Int {

    return when (category) {

        // ------------------------------------------------------
        // ⛽ EXPENSES → Fuel
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Fuel.FuelPurchase ->
            R.drawable.ic_fuel_pump

        is RecordCategory.ExpenseCategory.Fuel.ElectricCharge ->
            R.drawable.ic_electric_charge

        is RecordCategory.ExpenseCategory.Fuel.AdBlue ->
            R.drawable.ic_adblue

        is RecordCategory.ExpenseCategory.Fuel.FuelAdditives ->
            R.drawable.ic_fuel_additives

        // ------------------------------------------------------
        // 🛠 EXPENSES → Service
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Service.OilChange ->
            R.drawable.ic_oil_can

        is RecordCategory.ExpenseCategory.Service.OilFilter ->
            R.drawable.ic_oil_filter

        is RecordCategory.ExpenseCategory.Service.AirFilter ->
            R.drawable.ic_air_filter

        is RecordCategory.ExpenseCategory.Service.CabinFilter ->
            R.drawable.ic_cabin_filter

        is RecordCategory.ExpenseCategory.Service.FuelFilter ->
            R.drawable.ic_fuel_filter

        is RecordCategory.ExpenseCategory.Service.SparkPlugs ->
            R.drawable.ic_spark_plug

        is RecordCategory.ExpenseCategory.Service.Antirust ->
            R.drawable.ic_antirust

        is RecordCategory.ExpenseCategory.Service.TimingBelt ->
            R.drawable.ic_timing_belt

        is RecordCategory.ExpenseCategory.Service.TimingChain ->
            R.drawable.ic_timing_chain

        is RecordCategory.ExpenseCategory.Service.Antifreeze ->
            R.drawable.ic_antifreeze

        is RecordCategory.ExpenseCategory.Service.BrakeFluid ->
            R.drawable.ic_brake_fluid

        is RecordCategory.ExpenseCategory.Service.SteeringFluid ->
            R.drawable.ic_steering_fluid

        is RecordCategory.ExpenseCategory.Service.ValveAdjustment ->
            R.drawable.ic_valve

        is RecordCategory.ExpenseCategory.Service.SensorsGeneral ->
            R.drawable.ic_sensor

        is RecordCategory.ExpenseCategory.Service.GeneralService ->
            R.drawable.ic_service

        is RecordCategory.ExpenseCategory.Service.SmallService ->
            R.drawable.ic_service

        is RecordCategory.ExpenseCategory.Service.LargeService ->
            R.drawable.ic_service

        // ------------------------------------------------------
        // 🛞 EXPENSES → Tires
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Tires.TirePurchase ->
            R.drawable.ic_tires

        is RecordCategory.ExpenseCategory.Tires.WheelBalancing ->
            R.drawable.ic_wheel_balancing

        is RecordCategory.ExpenseCategory.Tires.WheelAlignment ->
            R.drawable.ic_wheel_alignment

        is RecordCategory.ExpenseCategory.Tires.TireRepair ->
            R.drawable.ic_tire_repair

        is RecordCategory.ExpenseCategory.Tires.Rims ->
            R.drawable.ic_rims

        is RecordCategory.ExpenseCategory.Tires.SeasonalTireChange ->
            R.drawable.ic_seasonal_tires

        // ------------------------------------------------------
        // 🧰 EXPENSES → Repairs
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Repairs.Brakes ->
            R.drawable.ic_brakes

        is RecordCategory.ExpenseCategory.Repairs.ShockAbsorbers ->
            R.drawable.ic_shock_absorber

        is RecordCategory.ExpenseCategory.Repairs.Battery ->
            R.drawable.ic_battery

        is RecordCategory.ExpenseCategory.Repairs.Starter ->
            R.drawable.ic_starter

        is RecordCategory.ExpenseCategory.Repairs.Alternator ->
            R.drawable.ic_alternator

        is RecordCategory.ExpenseCategory.Repairs.Clutch ->
            R.drawable.ic_clutch

        is RecordCategory.ExpenseCategory.Repairs.Gearbox ->
            R.drawable.ic_clutch

        is RecordCategory.ExpenseCategory.Repairs.Exhaust ->
            R.drawable.ic_exhaust

        is RecordCategory.ExpenseCategory.Repairs.Radiator ->
            R.drawable.ic_radiator

        is RecordCategory.ExpenseCategory.Repairs.ACService ->
            R.drawable.ic_ac_service

        is RecordCategory.ExpenseCategory.Repairs.ElectricalIssues ->
            R.drawable.ic_electrical

        is RecordCategory.ExpenseCategory.Repairs.SensorsSpecific ->
            R.drawable.ic_sensor

        is RecordCategory.ExpenseCategory.Repairs.FuelPump ->
            R.drawable.ic_fuel_pump

        is RecordCategory.ExpenseCategory.Repairs.Turbo ->
            R.drawable.ic_turbo

        is RecordCategory.ExpenseCategory.Repairs.DriveShafts ->
            R.drawable.ic_drive_shaft

        is RecordCategory.ExpenseCategory.Repairs.SteeringRack ->
            R.drawable.ic_drive_shaft

        is RecordCategory.ExpenseCategory.Repairs.Windows ->
            R.drawable.ic_car_door

        is RecordCategory.ExpenseCategory.Repairs.Multimedia ->
            R.drawable.ic_dashcam

        is RecordCategory.ExpenseCategory.Repairs.Wipers ->
            R.drawable.ic_windshield

        // ------------------------------------------------------
        // 📑 EXPENSES → Legal
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Legal.Insurance ->
            R.drawable.ic_insurance

        is RecordCategory.ExpenseCategory.Legal.Tolls ->
            R.drawable.ic_road_tolls

        is RecordCategory.ExpenseCategory.Legal.RoadTax ->
            R.drawable.ic_road_tax

        is RecordCategory.ExpenseCategory.Legal.KteoMot ->
            R.drawable.ic_kteo

        is RecordCategory.ExpenseCategory.Legal.LicensePlates ->
            R.drawable.ic_license_plate

        is RecordCategory.ExpenseCategory.Legal.Fines ->
            R.drawable.ic_fine

        is RecordCategory.ExpenseCategory.Legal.RegistrationFees ->
            R.drawable.ic_registration_fees

        // ------------------------------------------------------
        // 🚙 EXPENSES → Operational
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Operational.CarWash ->
            R.drawable.ic_car_wash

        is RecordCategory.ExpenseCategory.Operational.InteriorCleaning ->
            R.drawable.ic_interior_cleaning

        is RecordCategory.ExpenseCategory.Operational.BiologicalCleaning ->
            R.drawable.ic_biological_cleaning

        is RecordCategory.ExpenseCategory.Operational.Parking ->
            R.drawable.ic_parking

        is RecordCategory.ExpenseCategory.Operational.ParkingSubscription ->
            R.drawable.ic_parking_subscription

        is RecordCategory.ExpenseCategory.Operational.RoadsideAssistance ->
            R.drawable.ic_roadside_assistance

        is RecordCategory.ExpenseCategory.Operational.Accessories ->
            R.drawable.ic_accessories

        is RecordCategory.ExpenseCategory.Operational.Dashcam ->
            R.drawable.ic_dashcam

        is RecordCategory.ExpenseCategory.Operational.GpsTracker ->
            R.drawable.ic_gps_tracker

        is RecordCategory.ExpenseCategory.Operational.CleaningSupplies ->
            R.drawable.ic_cleaning_supplies

        // ------------------------------------------------------
        // ⚡ EXPENSES → EV SPECIAL
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.EVSpecial.Wallbox ->
            R.drawable.ic_electric_charge

        is RecordCategory.ExpenseCategory.EVSpecial.ChargerInstallation ->
            R.drawable.ic_electric_charge

        is RecordCategory.ExpenseCategory.EVSpecial.ChargingSubscription ->
            R.drawable.ic_electric_charge

        is RecordCategory.ExpenseCategory.EVSpecial.CablesAdapters ->
            R.drawable.ic_electrical

        is RecordCategory.ExpenseCategory.EVSpecial.BatteryCoolingService ->
            R.drawable.ic_battery

        // ------------------------------------------------------
        // 💥 EXPENSES → Damages
        // ------------------------------------------------------
        is RecordCategory.ExpenseCategory.Damages.BodyworkPaint ->
            R.drawable.ic_fender

        is RecordCategory.ExpenseCategory.Damages.WindshieldReplacement ->
            R.drawable.ic_windshield

        is RecordCategory.ExpenseCategory.Damages.WindowReplacement ->
            R.drawable.ic_windshield

        is RecordCategory.ExpenseCategory.Damages.BumpersPlastics ->
            R.drawable.ic_bumper

        is RecordCategory.ExpenseCategory.Damages.InteriorDamages ->
            R.drawable.ic_interior_damage

        // ------------------------------------------------------
        // 🛎 REMINDERS
        // ------------------------------------------------------
        is RecordCategory.ReminderCategory.MaintenanceReminder.OilChangeKmDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.MaintenanceReminder.OilChangeDateDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.MaintenanceReminder.TireChangeKmDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.MaintenanceReminder.TireChangeDateDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.MaintenanceReminder.GeneralServiceDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.MaintenanceReminder.BatteryCoolingServiceDue ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.LegalReminder.InsuranceReminder ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.LegalReminder.RoadTaxReminder ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.LegalReminder.KteoReminder ->
            R.drawable.ic_bell

        is RecordCategory.ReminderCategory.GeneralReminder ->
            R.drawable.ic_bell

        // ------------------------------------------------------
        // FALLBACKS
        // ------------------------------------------------------
        RecordCategory.UnknownExpense -> R.drawable.ic_expense_filled
        RecordCategory.UnknownReminder -> R.drawable.ic_bell
    }
}
