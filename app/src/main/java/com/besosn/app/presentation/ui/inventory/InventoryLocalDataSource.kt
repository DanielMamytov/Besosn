package com.besosn.app.presentation.ui.inventory

import android.content.Context
import com.besosn.app.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Lightweight access point for reading inventory information outside
 * of the Hilt-provided view models. Currently used by the Home
 * screen to display the real number of stored items.
 */
object InventoryLocalDataSource {

    suspend fun countItems(context: Context): Int = withContext(Dispatchers.IO) {
        DatabaseProvider.get(context).inventoryDao().countItems()
    }
}
