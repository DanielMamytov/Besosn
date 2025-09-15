package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory")
    fun getItems(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryEntity)

    @Delete
    suspend fun deleteItem(item: InventoryEntity)
}

