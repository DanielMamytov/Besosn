package com.besosn.app.domain.repository

import com.besosn.app.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
    fun getTeams(): Flow<List<Team>>
    suspend fun addTeam(team: Team)
    suspend fun deleteTeam(team: Team)
    suspend fun updateTeam(team: Team)
}

