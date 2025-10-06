
package com.vehicleman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val relationship: String, // π.χ. "Ιδιοκτήτης", "Συνεργάτης"
    val createdAt: Long = System.currentTimeMillis()
)