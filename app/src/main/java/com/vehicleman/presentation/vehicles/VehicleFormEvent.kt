// app/src/main/java/com/vehicleman/presentation/vehicles/VehicleFormEvent.kt
package com.vehicleman.presentation.vehicles

import java.util.Date

sealed interface VehicleFormEvent {
    data class OnNameChange(val value: String) : VehicleFormEvent
    data class OnMakeChange(val value: String) : VehicleFormEvent
    data class OnModelChange(val value: String) : VehicleFormEvent
    data class OnYearChange(val value: String) : VehicleFormEvent
    data class OnLicensePlateChange(val value: String) : VehicleFormEvent
    data class OnFuelTypeChange(val value: String) : VehicleFormEvent
    data class OnInitialOdometerChange(val value: String) : VehicleFormEvent
    data class OnRegistrationDateChange(val value: Date) : VehicleFormEvent

    // ΝΕΑ EVENTS ΓΙΑ ΤΑ ΔΙΑΣΤΗΜΑΤΑ ΣΥΝΤΗΡΗΣΗΣ
    data class OnOilChangeKmChange(val value: String) : VehicleFormEvent
    data class OnOilChangeDaysChange(val value: String) : VehicleFormEvent

    data object OnSaveVehicleClick : VehicleFormEvent
    data object NavigationDone : VehicleFormEvent
}