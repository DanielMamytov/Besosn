package com.besosn.app.data.repository

import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.data.model.InventoryEntity
import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : InventoryRepository {

    private val dao = db.inventoryDao()

    override fun getItems(): Flow<List<InventoryItem>> =
        dao.getItems().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addItem(item: InventoryItem) {
        dao.insertItem(item.toEntity())
    }

    override suspend fun deleteItem(item: InventoryItem) {
        dao.deleteItem(item.toEntity())
    }

    private fun InventoryEntity.toDomain() = InventoryItem(
        id = id,
        name = name,
        quantity = quantity,
        category = category,
        badge = badge,
        notes = notes
    )

    private fun InventoryItem.toEntity() = InventoryEntity(
        id = id,
        name = name,
        quantity = quantity,
        category = category,
        badge = badge,
        notes = notes
    )
}

