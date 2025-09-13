package com.besosn.app.domain.usecase.inventory

import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.domain.repository.InventoryRepository

class DeleteInventoryItemUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(item: InventoryItem) = repository.deleteItem(item)
}

