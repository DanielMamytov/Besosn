package com.besosn.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.domain.usecase.inventory.AddInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.DeleteInventoryItemUseCase
import com.besosn.app.domain.usecase.inventory.GetInventoryItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val addInventoryItemUseCase: AddInventoryItemUseCase,
    getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val deleteInventoryItemUseCase: DeleteInventoryItemUseCase
) : ViewModel() {

    val items: StateFlow<List<InventoryItem>> = getInventoryItemsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(item: InventoryItem) {
        viewModelScope.launch { addInventoryItemUseCase(item) }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch { deleteInventoryItemUseCase(item) }
    }
}

