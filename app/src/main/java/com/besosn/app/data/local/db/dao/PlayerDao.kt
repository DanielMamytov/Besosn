package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    fun getPlayers(): Flow<List<PlayerEntity>>

    @Insert
    suspend fun insertPlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)
}

