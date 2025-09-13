package com.besosn.app.domain.usecase.matches

import com.besosn.app.domain.model.Match
import com.besosn.app.domain.repository.MatchesRepository
import kotlinx.coroutines.flow.Flow

class GetMatchesUseCase(private val repository: MatchesRepository) {
    operator fun invoke(): Flow<List<Match>> = repository.getMatches()
}

