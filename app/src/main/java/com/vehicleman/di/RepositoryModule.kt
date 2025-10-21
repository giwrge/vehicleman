// app/src/main/java/com/vehicleman/di/RepositoryModule.kt
package com.vehicleman.di

//import com.vehicleman.data.dao.RecordDao
import com.vehicleman.data.dao.VehicleDao
//import com.vehicleman.data.repository.RecordRepositoryImpl
import com.vehicleman.data.repository.VehicleRepositoryImpl
//import com.vehicleman.domain.repositories.RecordRepository
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
        //  RecordDao: RecordDao
    ): VehicleRepository {
        return VehicleRepositoryImpl(
            vehicleDao //, RecordDao)
        )
    }
}
/*
    // ΠΑΡΟΧΗ: RecordRepository
   @Provides
   @Singleton
   fun provideRecordRepository(dao: RecordDao): RecordRepository {
      return RecordRepositoryImpl(dao)
    }

 */