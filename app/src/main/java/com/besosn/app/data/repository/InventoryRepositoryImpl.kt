package com.besosn.app.data.repository

import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor() : InventoryRepository {
    override fun getItems(): Flow<List<InventoryItem>> = flow { emit(emptyList()) }
    override suspend fun addItem(item: InventoryItem) { /* TODO */ }
    override suspend fun deleteItem(item: InventoryItem) { /* TODO */ }
}

