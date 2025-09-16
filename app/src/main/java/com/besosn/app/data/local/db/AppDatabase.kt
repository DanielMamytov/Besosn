package com.besosn.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.besosn.app.data.model.ArticleEntity
import com.besosn.app.data.model.InventoryEntity
import com.besosn.app.data.model.MatchEntity
import com.besosn.app.data.model.PlayerEntity
import com.besosn.app.data.model.TeamEntity
import com.besosn.app.data.local.db.dao.ArticleDao
import com.besosn.app.data.local.db.dao.InventoryDao
import com.besosn.app.data.local.db.dao.MatchDao
import com.besosn.app.data.local.db.dao.PlayerDao
import com.besosn.app.data.local.db.dao.TeamDao

@Database(
    entities = [
        TeamEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        InventoryEntity::class,
        ArticleEntity::class
    ],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun articleDao(): ArticleDao
}

