package com.vehicleman.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.vehicleman.data.mappers.toDriver
import com.vehicleman.data.mappers.toDriverEntity
import com.vehicleman.data.mappers.toRecord
import com.vehicleman.data.mappers.toRecordEntity
import com.vehicleman.data.mappers.toVehicle
import com.vehicleman.data.mappers.toVehicleEntity
import com.vehicleman.domain.model.AppBackup
import com.vehicleman.domain.repositories.DriverRepository
import com.vehicleman.domain.repositories.RecordRepository
import com.vehicleman.domain.repositories.User
import com.vehicleman.domain.repositories.UserPreferencesRepository
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.repositories.VehicleSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository,
    private val recordRepository: RecordRepository,
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val gson = Gson()

    val isNightMode: Flow<Boolean> = userPreferencesRepository.isNightMode
    val vehicleSortOrder: Flow<VehicleSortOrder> = userPreferencesRepository.vehicleSortOrder
    val user: StateFlow<User> = userPreferencesRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    fun setNightMode(isNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNightMode(isNightMode)
        }
    }

    fun setTestMode(isTestMode: Boolean) {
        viewModelScope.launch {
            val currentUser = user.first()
            userPreferencesRepository.saveUser(currentUser.copy(isTestMode = isTestMode))
        }
    }

    fun setVehicleSortOrder(sortOrder: VehicleSortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.setVehicleSortOrder(sortOrder)
        }
    }

    fun exportData(_format: String) {
        viewModelScope.launch {
            // ... export logic ...
        }
    }

    fun createBackup(): String {
        var jsonBackup = ""
        viewModelScope.launch {
            val backup = AppBackup(
                user = user.value,
                vehicles = vehicleRepository.getAllVehiclesList().map { it.toVehicleEntity() },
                records = recordRepository.getAllRecordsList().map { it.toRecordEntity() },
                drivers = driverRepository.getAllDriversList().map { it.toDriverEntity() },
                vehicleDriverRelations = driverRepository.getAllVehicleDriverCrossRefs()
            )
            jsonBackup = gson.toJson(backup)
            
            println("--- BACKUP CREATED (see next log for content) ---")
            println(jsonBackup)
        }
        return jsonBackup
    }

    fun restoreBackup(jsonBackup: String) {
        viewModelScope.launch {
            try {
                val backup = gson.fromJson(jsonBackup, AppBackup::class.java)
                
                // Clear existing data
                vehicleRepository.deleteAllVehicles()
                recordRepository.deleteAllRecords()
                driverRepository.deleteAllDrivers()
                driverRepository.deleteAllCrossRefs()
                
                // Restore data
                userPreferencesRepository.saveUser(backup.user)
                vehicleRepository.insertAllVehicles(backup.vehicles.map { it.toVehicle() })
                recordRepository.insertAllRecords(backup.records.map { it.toRecord() })
                driverRepository.insertAllDrivers(backup.drivers.map { it.toDriver() })
                driverRepository.insertAllCrossRefs(backup.vehicleDriverRelations)

                println("--- RESTORE COMPLETE ---")
            } catch (e: Exception) {
                println("--- RESTORE FAILED: ${e.message} ---")
            }
        }
    }

    fun importData(csvData: String) {
        // TODO: Implement CSV parsing logic and save to repositories
        println("--- IMPORTING DATA ---")
        println(csvData)
        println("--- END IMPORT ---")
    }
}