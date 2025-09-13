package com.besosn.app.domain.usecase.teams

import com.besosn.app.domain.model.Team
import com.besosn.app.domain.repository.TeamsRepository

class AddTeamUseCase(private val repository: TeamsRepository) {
    suspend operator fun invoke(team: Team) = repository.addTeam(team)
}

