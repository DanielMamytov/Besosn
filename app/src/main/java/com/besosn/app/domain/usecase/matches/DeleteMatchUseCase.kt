package com.besosn.app.domain.usecase.matches

import com.besosn.app.domain.model.Match
import com.besosn.app.domain.repository.MatchesRepository

class DeleteMatchUseCase(private val repository: MatchesRepository) {
    suspend operator fun invoke(match: Match) = repository.deleteMatch(match)
}

