package com.besosn.app.data.local.db.dao

import androidx.room.*
import com.besosn.app.data.model.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    fun getMatches(): Flow<List<MatchEntity>>

    @Insert
    suspend fun insertMatch(match: MatchEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun countMatches(): Int

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}

