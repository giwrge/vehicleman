package com.vehicleman.data.repository

import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.VehicleEntity
// Υποθέτουμε ότι υπάρχουν οι απαραίτητες συναρτήσεις επέκτασης (mappers)
import com.vehicleman.data.mappers.toDomain
import com.vehicleman.data.mappers.toEntity
import com.vehicleman.domain.repositories.VehicleRepository
import com.vehicleman.domain.model.Vehicle // ΔΙΟΡΘΩΘΗΚΕ: Σωστό import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Υλοποίηση του VehicleRepository, υπεύθυνη για την αλληλεπίδραση με το Data Layer (Room).
 * Χρησιμοποιεί τους mappers για τη μετατροπή μεταξύ Entity και Domain Model (Clean Architecture).
 *
 * @param vehicleDao Το Data Access Object για τη βάση δεδομένων.
 */
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    /**
     * Επιστρέφει ένα Flow με όλα τα οχήματα.
     */
    override fun getAllVehicles(): Flow<List<Vehicle>> {
        // Μετατρέπει το Flow<List<Entity>> σε Flow<List<Domain Model>>
        return vehicleDao.getAllVehicles().map { entities ->
            entities.map(VehicleEntity::toDomain)
        }
    }

    /**
     * Λαμβάνει ένα όχημα (Vehicle) με βάση το ID του.
     */
    override suspend fun getVehicleById(id: String): Vehicle? {
        return vehicleDao.getVehicleById(id)?.toDomain()
    }

    /**
     * Αποθηκεύει (εισάγει ή ενημερώνει) ένα όχημα.
     */
    override suspend fun saveVehicle(vehicle: Vehicle) {
        // Μετατρέπει το Domain Model σε Entity πριν την εισαγωγή
        vehicleDao.insertVehicle(vehicle.toEntity())
     //   override suspend fun saveVehicle(vehicle: Vehicle) {
      //      // Αν θέλουμε να κάνουμε insert ή update με βάση to IF διότι θα γράφει από πάνω συνέχεια
      //      val existing = vehicleDao.getVehicleById(vehicle.id)
      //      if (existing != null) {
      //          vehicleDao.updateVehicle(vehicle.toEntity())
      //      } else {
      //          vehicleDao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun insertVehicle(vehicle: Vehicle) {
        vehicleDao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.updateVehicle(vehicle.toEntity())
    }
    /**
     * Διαγράφει ένα μόνο όχημα.
     */
    override suspend fun deleteVehicle(vehicle: Vehicle) {
        // Μετατρέπει το Domain Model σε Entity πριν τη διαγραφή
        vehicleDao.deleteVehicle(vehicle.toEntity())
    }

    /**
     * Διαγράφει μαζικά οχήματα με βάση ένα Set από IDs.
     */
    override suspend fun deleteVehiclesByIds(vehicleIds: Set<String>) {
        // Το DAO αναμένει List<String>, οπότε το μετατρέπουμε
        vehicleDao.deleteVehiclesByIds(vehicleIds.toList())
    }

    /**
     * Επιστρέφει τον συνολικό αριθμό των καταχωρημένων οχημάτων.
     * Καλεί την αντίστοιχη συνάρτηση του DAO.
     */
    override suspend fun getVehicleCount(): Int {
        return vehicleDao.getVehicleCount()
    }
}
