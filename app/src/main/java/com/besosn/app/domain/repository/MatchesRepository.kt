package com.besosn.app.domain.repository

import com.besosn.app.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface MatchesRepository {
    fun getMatches(): Flow<List<Match>>
    suspend fun addMatch(match: Match)
    suspend fun deleteMatch(match: Match)
}

