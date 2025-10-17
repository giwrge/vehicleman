// app/src/main/java/com/vehicleman/di/DatabaseModule.kt
package com.vehicleman.di

import android.app.Application
import androidx.room.Room
import com.vehicleman.data.VehicleDatabase
import com.vehicleman.data.dao.RecordDao
import com.vehicleman.data.dao.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVehicleDatabase(app: Application): VehicleDatabase {
        return Room.databaseBuilder(
            app,
            VehicleDatabase::class.java,
            VehicleDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideVehicleDao(db: VehicleDatabase): VehicleDao {
        return db.vehicleDao()
    }

    // ΠΑΡΟΧΗ: MaintenanceRecordDao
    @Provides
    @Singleton
    fun provideMaintenanceRecordDao(db: VehicleDatabase): RecordDao {
        return db.RecordDao()
    }
}