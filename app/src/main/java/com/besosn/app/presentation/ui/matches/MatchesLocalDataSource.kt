package com.besosn.app.presentation.ui.matches

import android.content.Context
import androidx.annotation.DrawableRes
import com.besosn.app.R
import com.besosn.app.data.local.db.DatabaseProvider
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provides access to match data that is stored locally in SharedPreferences
 * together with the predefined demo matches that ship with the app.
 */
object MatchesLocalDataSource {

    fun loadMatches(context: Context): List<MatchModel> {
        val matches = mutableListOf<MatchModel>()
        matches += getDefaultMatches()
        matches += loadSavedMatches(context)
        return matches
    }

    suspend fun countMatches(context: Context): Int = withContext(Dispatchers.IO) {
        val defaultMatches = getDefaultMatches().size
        val savedMatches = loadSavedMatches(context).size
        val storedMatches = runCatching {
            DatabaseProvider.get(context).matchDao().countMatches()
        }.getOrDefault(0)

        defaultMatches + savedMatches + storedMatches
    }

    internal fun loadSavedMatches(context: Context): List<MatchModel> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(PREFS_KEY_MATCHES, null) ?: return emptyList()

        return try {
            val array = JSONArray(raw)
            val result = mutableListOf<MatchModel>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val homeTeam = obj.optString("homeTeam")
                val awayTeam = obj.optString("awayTeam")
                if (homeTeam.isBlank() || awayTeam.isBlank()) continue

                val timestamp = obj.optLong("timestamp", -1L)
                val dateMillis = if (timestamp > 0L) {
                    timestamp
                } else {
                    parseDateTime(obj.optString("date"), obj.optString("time")) ?: continue
                }

                val homeScore = (obj.opt("homeGoals") as? Number)?.toInt()
                val awayScore = (obj.opt("awayGoals") as? Number)?.toInt()

                result += MatchModel(
                    id = SAVED_MATCH_ID_OFFSET + i,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam,
                    homeIconRes = resolveTeamIcon(homeTeam),
                    awayIconRes = resolveTeamIcon(awayTeam),
                    date = dateMillis,
                    homeScore = homeScore,
                    awayScore = awayScore,
                )
            }
            result
        } catch (_: JSONException) {
            emptyList()
        }
    }

    private fun getDefaultMatches(): List<MatchModel> {
        val now = Calendar.getInstance()
        val past = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val future = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 3) }

        return listOf(
            MatchModel(
                id = 1,
                homeTeam = "Barcelona",
                awayTeam = "Real Madrid",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = past.timeInMillis,
                homeScore = 1,
                awayScore = 2,
            ),
            MatchModel(
                id = 2,
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = future.timeInMillis,
            ),
        )
    }

    private fun parseDateTime(date: String?, time: String?): Long? {
        if (date.isNullOrBlank() || time.isNullOrBlank()) return null
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            format.parse("$date $time")?.time
        } catch (_: Exception) {
            null
        }
    }

    @DrawableRes
    internal fun resolveTeamIcon(teamName: String): Int {
        return when (teamName.trim().lowercase(Locale.getDefault())) {
            "barcelona" -> R.drawable.vdgdsgfds
            "real madrid" -> R.drawable.jkljfsjfls
            "arsenal" -> R.drawable.vdgdsgfds
            "chelsea" -> R.drawable.jkljfsjfls
            else -> R.drawable.ball
        }
    }

    private const val SAVED_MATCH_ID_OFFSET = 1000
    private const val PREFS_NAME = "matches_prefs"
    private const val PREFS_KEY_MATCHES = "matches"
}
