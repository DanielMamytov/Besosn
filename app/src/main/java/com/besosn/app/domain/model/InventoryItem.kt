package com.besosn.app.domain.model

import java.io.Serializable

/**
 * Domain model representing a single item in the inventory.
 */
data class InventoryItem(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val category: String,
    val badge: String,
    val notes: String,
    val photoUri: String? = null,
) : Serializable

