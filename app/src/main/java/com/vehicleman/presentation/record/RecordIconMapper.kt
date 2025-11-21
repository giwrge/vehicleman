package com.vehicleman.presentation.record

import com.vehicleman.R

fun mapCategoryToIcon(category: RecordCategory): Int {
    return when (category) {
        RecordCategory.FUEL_PURCHASE -> R.drawable.ic_fuel_pump
        RecordCategory.ELECTRIC_CHARGE -> R.drawable.ic_electric_charge
        RecordCategory.ADBLUE -> R.drawable.ic_adblue
        RecordCategory.FUEL_ADDITIVES -> R.drawable.ic_fuel_additives
        RecordCategory.OIL_CHANGE -> R.drawable.ic_oil_can
        RecordCategory.OIL_FILTER -> R.drawable.ic_oil_filter
        RecordCategory.AIR_FILTER -> R.drawable.ic_air_filter
        RecordCategory.CABIN_FILTER -> R.drawable.ic_cabin_filter
        RecordCategory.FUEL_FILTER -> R.drawable.ic_fuel_filter
        RecordCategory.SPARK_PLUGS -> R.drawable.ic_spark_plug
        RecordCategory.ANTIRUST -> R.drawable.ic_antirust
        RecordCategory.TIMING_BELT -> R.drawable.ic_timing_belt
        RecordCategory.TIMING_CHAIN -> R.drawable.ic_timing_chain
        RecordCategory.ANTIFREEZE -> R.drawable.ic_antifreeze
        RecordCategory.BRAKE_FLUID -> R.drawable.ic_brake_fluid
        RecordCategory.STEERING_FLUID -> R.drawable.ic_steering_fluid
        RecordCategory.VALVE_ADJUSTMENT -> R.drawable.ic_valve
        RecordCategory.SENSORS_GENERAL -> R.drawable.ic_sensor
        RecordCategory.GENERAL_SERVICE -> R.drawable.ic_service
        RecordCategory.SMALL_SERVICE -> R.drawable.ic_service
        RecordCategory.LARGE_SERVICE -> R.drawable.ic_service
        RecordCategory.TIRE_PURCHASE -> R.drawable.ic_tires
        RecordCategory.WHEEL_BALANCING -> R.drawable.ic_wheel_balancing
        RecordCategory.WHEEL_ALIGNMENT -> R.drawable.ic_wheel_alignment
        RecordCategory.TIRE_REPAIR -> R.drawable.ic_tire_repair
        RecordCategory.RIMS -> R.drawable.ic_rims
        RecordCategory.SEASONAL_TIRE_CHANGE -> R.drawable.ic_seasonal_tires
        RecordCategory.BRAKES -> R.drawable.ic_brakes
        RecordCategory.SHOCK_ABSORBERS -> R.drawable.ic_shock_absorber
        RecordCategory.BATTERY -> R.drawable.ic_battery
        RecordCategory.STARTER -> R.drawable.ic_starter
        RecordCategory.ALTERNATOR -> R.drawable.ic_alternator
        RecordCategory.CLUTCH -> R.drawable.ic_clutch
        RecordCategory.GEARBOX -> R.drawable.ic_clutch
        RecordCategory.EXHAUST -> R.drawable.ic_exhaust
        RecordCategory.RADIATOR -> R.drawable.ic_radiator
        RecordCategory.AC_SERVICE -> R.drawable.ic_ac_service
        RecordCategory.ELECTRICAL_ISSUES -> R.drawable.ic_electrical
        RecordCategory.SENSORS_SPECIFIC -> R.drawable.ic_sensor
        RecordCategory.FUEL_PUMP -> R.drawable.ic_fuel_pump
        RecordCategory.TURBO -> R.drawable.ic_turbo
        RecordCategory.DRIVE_SHAFTS -> R.drawable.ic_drive_shaft
        RecordCategory.STEERING_RACK -> R.drawable.ic_drive_shaft
        RecordCategory.WINDOWS -> R.drawable.ic_car_door
        RecordCategory.MULTIMEDIA -> R.drawable.ic_dashcam
        RecordCategory.WIPERS -> R.drawable.ic_windshield
        RecordCategory.INSURANCE -> R.drawable.ic_insurance
        RecordCategory.TOLLS -> R.drawable.ic_road_tolls
        RecordCategory.ROAD_TAX -> R.drawable.ic_road_tax
        RecordCategory.KTEO_MOT -> R.drawable.ic_kteo
        RecordCategory.LICENSE_PLATES -> R.drawable.ic_license_plate
        RecordCategory.FINES -> R.drawable.ic_fine
        RecordCategory.REGISTRATION_FEES -> R.drawable.ic_registration_fees
        RecordCategory.CAR_WASH -> R.drawable.ic_car_wash
        RecordCategory.INTERIOR_CLEANING -> R.drawable.ic_interior_cleaning
        RecordCategory.BIOLOGICAL_CLEANING -> R.drawable.ic_biological_cleaning
        RecordCategory.PARKING -> R.drawable.ic_parking
        RecordCategory.PARKING_SUBSCRIPTION -> R.drawable.ic_parking_subscription
        RecordCategory.ROADSIDE_ASSISTANCE -> R.drawable.ic_roadside_assistance
        RecordCategory.DASHCAM -> R.drawable.ic_dashcam
        RecordCategory.GPS_TRACKER -> R.drawable.ic_gps_tracker
        RecordCategory.CLEANING_SUPPLIES -> R.drawable.ic_cleaning_supplies
        RecordCategory.WALLBOX -> R.drawable.ic_electric_charge
        RecordCategory.CHARGER_INSTALLATION -> R.drawable.ic_electric_charge
        RecordCategory.CHARGING_SUBSCRIPTION -> R.drawable.ic_electric_charge
        RecordCategory.CABLES_ADAPTERS -> R.drawable.ic_electrical
        RecordCategory.BATTERY_COOLING_SERVICE -> R.drawable.ic_battery
        RecordCategory.BODYWORK_PAINT -> R.drawable.ic_fender
        RecordCategory.WINDSHIELD_REPLACEMENT -> R.drawable.ic_windshield
        RecordCategory.WINDOW_REPLACEMENT -> R.drawable.ic_windshield
        RecordCategory.BUMPERS_PLASTICS -> R.drawable.ic_bumper
        RecordCategory.INTERIOR_DAMAGES -> R.drawable.ic_interior_damage
        RecordCategory.FUEL_UP -> R.drawable.ic_fuel_pump
        RecordCategory.UNKNOWN_EXPENSE -> R.drawable.ic_expense_filled
        RecordCategory.UNKNOWN_REMINDER -> R.drawable.ic_bell

    }
}
