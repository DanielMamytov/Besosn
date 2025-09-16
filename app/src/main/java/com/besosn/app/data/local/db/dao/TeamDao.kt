package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.TeamEntity
@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    suspend fun getTeams(): List<TeamEntity>

    @Insert
    suspend fun insertTeam(team: TeamEntity): Long

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()
}

