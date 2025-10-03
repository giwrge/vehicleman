package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Οντότητα Room που αντιπροσωπεύει ένα Συμβάν (π.χ., ανεφοδιασμός, σέρβις)
 * στη βάση δεδομένων.
 *
 * @param id Μοναδικό αναγνωριστικό συμβάντος (UUID string).
 * @param vehicleId Το ID του οχήματος στο οποίο ανήκει το συμβάν.
 * @param date Ημερομηνία/ώρα του συμβάντος (ως timestamp).
 * @param odometer Η ένδειξη του οδόμετρου κατά τη στιγμή του συμβάντος.
 * @param type Ο τύπος του συμβάντος (π.χ., "Refuel", "Service", "Repair").
 * @param notes Σημειώσεις για το συμβάν.
 */
@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val date: Long = System.currentTimeMillis(),
    val odometer: Int,
    val type: String,
    val notes: String? = null,
    // Πεδία ειδικά για ανεφοδιασμό (μπορεί να είναι null για άλλα συμβάντα)
    val fuelQuantityLiters: Double? = null,
    val pricePerLiter: Double? = null,
    val totalCost: Double? = null
)
