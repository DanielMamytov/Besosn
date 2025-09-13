package com.besosn.app.domain.repository

import com.besosn.app.domain.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getItems(): Flow<List<InventoryItem>>
    suspend fun addItem(item: InventoryItem)
    suspend fun deleteItem(item: InventoryItem)
}

