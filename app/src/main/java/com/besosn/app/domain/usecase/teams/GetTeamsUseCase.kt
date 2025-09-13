package com.besosn.app.domain.usecase.teams

import com.besosn.app.domain.model.Team
import com.besosn.app.domain.repository.TeamsRepository
import kotlinx.coroutines.flow.Flow

class GetTeamsUseCase(private val repository: TeamsRepository) {
    operator fun invoke(): Flow<List<Team>> = repository.getTeams()
}

