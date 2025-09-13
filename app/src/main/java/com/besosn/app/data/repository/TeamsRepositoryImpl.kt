package com.besosn.app.data.repository

import com.besosn.app.domain.model.Team
import com.besosn.app.domain.repository.TeamsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TeamsRepositoryImpl @Inject constructor() : TeamsRepository {
    override fun getTeams(): Flow<List<Team>> = flow { emit(emptyList()) }
    override suspend fun addTeam(team: Team) { /* TODO */ }
    override suspend fun deleteTeam(team: Team) { /* TODO */ }
    override suspend fun updateTeam(team: Team) { /* TODO */ }
}

