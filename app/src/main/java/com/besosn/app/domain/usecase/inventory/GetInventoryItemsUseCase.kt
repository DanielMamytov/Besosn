package com.besosn.app.domain.usecase.inventory

import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetInventoryItemsUseCase(private val repository: InventoryRepository) {
    operator fun invoke(): Flow<List<InventoryItem>> = repository.getItems()
}

