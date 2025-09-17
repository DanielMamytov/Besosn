package com.besosn.app.presentation.ui.teams

import android.content.Context
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Provides access to locally stored team data along with the
 * predefined demo teams that ship with the app. The logic is shared
 * between the Teams screen and the Home dashboard so both display
 * consistent data.
 */
object TeamsLocalDataSource {

    private val seedMutex = Mutex()

    suspend fun loadTeams(context: Context): List<TeamModel> = withContext(Dispatchers.IO) {
        val db = DatabaseProvider.get(context)
        ensureDefaultTeams(db)

        val teamDao = db.teamDao()
        val playerDao = db.playerDao()
        val teams = teamDao.getTeams()
        val players = playerDao.getPlayers()
        val playersByTeam = players.groupBy { it.teamId }

        teams.map { entity ->
            entity.toModel(playersByTeam[entity.id].orEmpty())
        }
    }

    suspend fun countTeams(context: Context): Int = withContext(Dispatchers.IO) {
        val db = DatabaseProvider.get(context)
        ensureDefaultTeams(db)
        db.teamDao().getTeams().size
    }

    private suspend fun ensureDefaultTeams(db: AppDatabase) {
        seedMutex.withLock {
            val teamDao = db.teamDao()
            if (teamDao.getTeams().isNotEmpty()) return

            val playerDao = db.playerDao()
            defaultTeams().forEach { team ->
                val teamId = teamDao.insertTeam(team.toEntity()).toInt()
                playerDao.insertPlayers(team.players.map { it.toEntity(teamId) })
            }
        }
    }

    private fun defaultTeams(): List<TeamModel> {
        val team1 = TeamModel(
            name = "YoungTeam",
            city = "New York",
            foundedYear = 2024,
            notes = "Default team",
            players = listOf(PlayerModel("Alex Finch", "FW", 10)),
            iconRes = R.drawable.ball,
            iconUri = null,
            isDefault = true,
        )
        val team2 = TeamModel(
            name = "TalentTeam",
            city = "Chicago",
            foundedYear = 2021,
            notes = "Default team",
            players = listOf(PlayerModel("Yacob Sunny", "GK", 1)),
            iconRes = R.drawable.ball,
            iconUri = null,
            isDefault = true,
        )
        return listOf(team1, team2)
    }
}
