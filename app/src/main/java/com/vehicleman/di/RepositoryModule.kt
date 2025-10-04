package com.vehicleman.di

import com.vehicleman.data.repository.VehicleRepositoryImpl // ΣΩΣΤΟ PACKAGE (singular 'repository')
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module για τη σύνδεση της διεπαφής VehicleRepository με την υλοποίησή της.
 *
 * Αυτό το Module λέει στο Hilt: "Όταν κάποιος ζητήσει ένα VehicleRepository, δώσε του
 * ένα VehicleRepositoryImpl".
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        // Η παράμετρος πρέπει να είναι η ΥΛΟΠΟΙΗΣΗ (Impl)
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository // Ο τύπος επιστροφής πρέπει να είναι η ΔΙΕΠΑΦΗ
}
