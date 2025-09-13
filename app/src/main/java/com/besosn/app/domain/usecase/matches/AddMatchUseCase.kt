package com.besosn.app.domain.usecase.matches

import com.besosn.app.domain.model.Match
import com.besosn.app.domain.repository.MatchesRepository

class AddMatchUseCase(private val repository: MatchesRepository) {
    suspend operator fun invoke(match: Match) = repository.addMatch(match)
}

