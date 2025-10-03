package com.vehicleman.di

import com.vehicleman.data.repository.VehicleRepositoryImpl
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module του Hilt για τη δέσμευση Interfaces σε συγκεκριμένες υλοποιήσεις (π.χ. Repository).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Δεσμεύει το VehicleRepository Interface στην υλοποίησή του (VehicleRepositoryImpl).
     */
    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository
}
