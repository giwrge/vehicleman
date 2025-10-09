// app/src/main/java/com/vehicleman/di/RepositoryModule.kt
package com.vehicleman.di

import com.vehicleman.data.dao.MaintenanceRecordDao
import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.repository.MaintenanceRecordRepositoryImpl
import com.vehicleman.data.repository.VehicleRepositoryImpl
import com.vehicleman.domain.repositories.MaintenanceRecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    // VehicleRepositoryImpl: Χρειάζεται και τα δύο DAO για το cascading delete
    fun provideVehicleRepository(
        vehicleDao: VehicleDao,
        maintenanceRecordDao: MaintenanceRecordDao
    ): VehicleRepository {
        return VehicleRepositoryImpl(vehicleDao, maintenanceRecordDao)
    }

    // ΠΑΡΟΧΗ: MaintenanceRecordRepository
    @Provides
    @Singleton
    fun provideMaintenanceRecordRepository(dao: MaintenanceRecordDao): MaintenanceRecordRepository {
        return MaintenanceRecordRepositoryImpl(dao)
    }
}