package com.vehicleman.di

import android.content.Context
import androidx.room.Room
import com.vehicleman.data.VehicleDatabase
import com.vehicleman.data.dao.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module για την παροχή (provision) της βάσης δεδομένων Room
 * και του VehicleDao σε όλη την εφαρμογή.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Παρέχει την κεντρική βάση δεδομένων Room.
     * @param context Το Context της εφαρμογής, παρέχεται από το Hilt.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VehicleDatabase {
        return Room.databaseBuilder(
            context,
            VehicleDatabase::class.java,
            VehicleDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Χρήσιμο για αρχική ανάπτυξη
            .build()
    }

    /**
     * Παρέχει το Data Access Object (DAO) για τα Οχήματα.
     * @param database Η VehicleDatabase που μόλις δημιουργήθηκε.
     */
    @Provides
    fun provideVehicleDao(database: VehicleDatabase): VehicleDao {
        return database.vehicleDao()
    }
}
