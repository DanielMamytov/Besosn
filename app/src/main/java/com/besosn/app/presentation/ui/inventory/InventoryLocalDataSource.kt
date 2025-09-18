package com.besosn.app.presentation.ui.inventory

import android.content.Context
import com.besosn.app.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object InventoryLocalDataSource {

    suspend fun countItems(context: Context): Int = withContext(Dispatchers.IO) {
        DatabaseProvider.get(context).inventoryDao().countItems()
    }
}
