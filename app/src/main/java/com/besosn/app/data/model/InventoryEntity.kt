package com.besosn.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val category: String,
    val badge: String,
    val notes: String,
    val photoUri: String? = null,
)

