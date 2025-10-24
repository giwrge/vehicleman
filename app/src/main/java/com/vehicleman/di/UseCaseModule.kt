package com.vehicleman.di

import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.use_case.DeleteRecord
import com.vehicleman.domain.use_case.GetLatestOdometer
import com.vehicleman.domain.use_case.GetRecord
import com.vehicleman.domain.use_case.GetRecords
import com.vehicleman.domain.use_case.GetVehicleConsumption
import com.vehicleman.domain.use_case.GetVehicleStatistics
import com.vehicleman.domain.use_case.RecordUseCases
import com.vehicleman.domain.use_case.SaveRecord
import com.vehicleman.domain.use_case.StatisticsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideRecordUseCases(repository: RecordRepository): RecordUseCases {
        return RecordUseCases(
            getRecords = GetRecords(repository),
            getRecord = GetRecord(repository),
            saveRecord = SaveRecord(repository),
            deleteRecord = DeleteRecord(repository),
            getLatestOdometer = GetLatestOdometer(repository)
        )
    }

    @Provides
    @Singleton
    fun provideStatisticsUseCases(vehicleRepository: VehicleRepository, recordRepository: RecordRepository): StatisticsUseCases {
        return StatisticsUseCases(
            getVehicleStatistics = GetVehicleStatistics(vehicleRepository),
            getVehicleConsumption = GetVehicleConsumption(recordRepository)
        )
    }
}
