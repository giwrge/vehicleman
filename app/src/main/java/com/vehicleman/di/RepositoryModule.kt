package com.vehicleman.di

import com.vehicleman.data.repository.DriverRepositoryImpl
import com.vehicleman.data.repository.RecordRepositoryImpl
import com.vehicleman.data.repository.UserPreferencesRepositoryImpl
import com.vehicleman.data.repository.VehicleRepositoryImpl
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
    
    @Binds
    @Singleton
    abstract fun bindDriverRepository(impl: DriverRepositoryImpl): DriverRepository
}