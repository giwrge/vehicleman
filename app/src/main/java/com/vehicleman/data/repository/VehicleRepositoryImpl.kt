package com.vehicleman.data.repository

import com.vehicleman.data.dao.VehicleDao
import com.vehicleman.data.entities.VehicleEntity
import com.vehicleman.domain.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Υλοποίηση του VehicleRepository.
 * * Αυτή η κλάση χρησιμοποιεί το VehicleDao για να επικοινωνήσει με τη βάση δεδομένων
 * και υλοποιεί το Interface VehicleRepository, όπως ορίζεται στο Domain layer.
 */
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    // --- Υλοποίηση Interface ---

    // ΣΗΜΕΙΩΣΗ: Αφαιρέθηκαν οι Mappers (toDomain/toEntity) καθώς το Repository
    // χρησιμοποιεί πλέον απευθείας το VehicleEntity (όπως ορίσατε).

    override fun getAllVehicles(): Flow<List<VehicleEntity>> {
        // Επιστρέφει απευθείας το Flow από το DAO.
        return vehicleDao.getAllVehicles()
    }

    override suspend fun getVehicleById(vehicleId: String): VehicleEntity? {
        return vehicleDao.getVehicleById(vehicleId)
    }

    override suspend fun saveVehicle(vehicle: VehicleEntity): Long {
        // Το DAO χρησιμοποιεί ήδη onConflict = REPLACE, καλύπτοντας insert και update.
        return vehicleDao.insertVehicle(vehicle)
    }

    override suspend fun deleteVehicle(vehicle: VehicleEntity) {
        vehicleDao.deleteVehicle(vehicle)
    }

    override suspend fun deleteVehiclesByIds(vehicleIds: Set<String>) {
        vehicleDao.deleteVehiclesByIds(vehicleIds)
    }

    // ΣΗΜΕΙΩΣΗ: Οι συναρτήσεις 'updateVehicle', 'insertVehicle' και 'getVehicleCount'
    // αφαιρέθηκαν/διορθώθηκαν για να ταιριάζουν με το Interface που μου παρείχατε.
    // Η λειτουργία update Vehicle καλύπτεται από τη saveVehicle.
}
