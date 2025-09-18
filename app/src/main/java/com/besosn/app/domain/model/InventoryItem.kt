package com.besosn.app.domain.model

import java.io.Serializable


data class InventoryItem(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val category: String,
    val badge: String,
    val notes: String,
    val photoUri: String? = null,
) : Serializable

