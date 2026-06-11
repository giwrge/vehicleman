package com.vehicleman.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // Αφαιρέσαμε τις χειροκίνητες παροχές (Provides) για UseCases.
    // Επειδή τα RecordUseCases, StatisticsUseCases και όλα τα επιμέρους Use Cases
    // έχουν ήδη @Inject constructor στις κλάσεις τους, το Hilt τα βρίσκει αυτόματα.
    // Αυτό λύνει το "δομικό" πρόβλημα των Duplicate Bindings που σταματά το build.
}
