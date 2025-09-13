package com.besosn.app.data.repository

import com.besosn.app.domain.model.Match
import com.besosn.app.domain.repository.MatchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MatchesRepositoryImpl @Inject constructor() : MatchesRepository {
    override fun getMatches(): Flow<List<Match>> = flow { emit(emptyList()) }
    override suspend fun addMatch(match: Match) { /* TODO */ }
    override suspend fun deleteMatch(match: Match) { /* TODO */ }
}

