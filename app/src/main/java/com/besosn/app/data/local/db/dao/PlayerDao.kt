package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.PlayerEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    suspend fun getPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    suspend fun getPlayersForTeam(teamId: Int): List<PlayerEntity>

    @Insert
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Query("DELETE FROM players WHERE teamId = :teamId")
    suspend fun deletePlayersByTeam(teamId: Int)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)
}

