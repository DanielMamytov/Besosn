package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getTeams(): Flow<List<TeamEntity>>

    @Insert
    suspend fun insertTeam(team: TeamEntity)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Update
    suspend fun updateTeam(team: TeamEntity)
}

