package com.vehicleman.di

import com.vehicleman.data.repository.VehicleRepositoryImpl
import com.vehicleman.domain.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module για τη σύνδεση των Repository Interfaces με τις Implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Δεσμεύει το Interface VehicleRepository με την Υλοποίηση VehicleRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository
}
