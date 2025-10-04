package com.vehicleman.data.mappers

import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.model.Vehicle

/**
 * Mapping extension function για τη μετατροπή του Domain Model (Vehicle)
 * στο Data Layer Entity (VehicleEntity) για αποθήκευση στο Room.
 */
fun Vehicle.toEntity(): VehicleEntity {
    return VehicleEntity(
        id = this.id,
        name = this.name,
        make = this.make,
        model = this.model,
        year = this.year,
        licensePlate = this.licensePlate,
        fuelType = this.fuelType,
        initialOdometer = this.initialOdometer,
        registrationDate = this.registrationDate
    )
}

/**
 * Mapping extension function για τη μετατροπή του Data Layer Entity (VehicleEntity)
 * πίσω στο Domain Model (Vehicle) για χρήση στο UI/ViewModel.
 *
 * Αυτή η συνάρτηση διορθώνει το σφάλμα "Argument type mismatch".
 */
fun VehicleEntity.toDomain(): Vehicle {
    return Vehicle(
        id = this.id,
        name = this.name,
        make = this.make,
        model = this.model,
        year = this.year,
        licensePlate = this.licensePlate,
        fuelType = this.fuelType,
        initialOdometer = this.initialOdometer,
        registrationDate = this.registrationDate
    )
}
