package com.besosn.app.presentation.ui.matches

import android.content.Context
import androidx.annotation.DrawableRes
import com.besosn.app.R
import com.besosn.app.data.local.db.DatabaseProvider
import com.besosn.app.data.model.MatchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
                val city = obj.optString("city").takeIf { it.isNotBlank() }
                val notes = obj.optString("notes").takeIf { it.isNotBlank() }
                val homeIconUri = obj.optString("homePhotoUri").takeIf { it.isNotBlank() }
                val awayIconUri = obj.optString("awayPhotoUri").takeIf { it.isNotBlank() }

                result += MatchModel(
                    id = LEGACY_MATCH_ID_OFFSET + i,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam,
                    homeIconRes = resolveTeamIcon(homeTeam),
                    awayIconRes = resolveTeamIcon(awayTeam),
                    homeIconUri = homeIconUri,
                    awayIconUri = awayIconUri,
                    date = dateMillis,
                    homeScore = homeScore,
                    awayScore = awayScore,
                    city = city,
                    notes = notes,
                )
            }
            result
        } catch (_: JSONException) {
            emptyList()
        }
    }

    internal fun updateSavedMatch(context: Context, index: Int, entity: MatchEntity): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(PREFS_KEY_MATCHES, null) ?: return false

        return try {
            val array = JSONArray(raw)
            if (index < 0 || index >= array.length()) return false

            val obj = array.optJSONObject(index) ?: JSONObject()
            obj.put("homeTeam", entity.homeTeamName)
            obj.put("awayTeam", entity.awayTeamName)
            obj.put("timestamp", entity.date)

            if (entity.homeGoals != null) obj.put("homeGoals", entity.homeGoals) else obj.remove("homeGoals")
            if (entity.awayGoals != null) obj.put("awayGoals", entity.awayGoals) else obj.remove("awayGoals")

            if (entity.city.isNotBlank()) {
                obj.put("city", entity.city)
            } else {
                obj.remove("city")
            }
            if (entity.notes.isNotBlank()) {
                obj.put("notes", entity.notes)
            } else {
                obj.remove("notes")
            }

            if (!entity.homePhotoUri.isNullOrBlank()) {
                obj.put("homePhotoUri", entity.homePhotoUri)
            } else {
                obj.remove("homePhotoUri")
            }
            if (!entity.awayPhotoUri.isNullOrBlank()) {
                obj.put("awayPhotoUri", entity.awayPhotoUri)
            } else {
                obj.remove("awayPhotoUri")
            }

            array.put(index, obj)
            prefs.edit().putString(PREFS_KEY_MATCHES, array.toString()).apply()
            true
        } catch (_: JSONException) {
            false
        }
    }

    internal fun deleteSavedMatch(context: Context, index: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(PREFS_KEY_MATCHES, null) ?: return false

        return try {
            val array = JSONArray(raw)
            if (index < 0 || index >= array.length()) return false

            val updated = JSONArray()
            for (i in 0 until array.length()) {
                if (i == index) continue
                val obj = array.optJSONObject(i) ?: continue
                updated.put(obj)
            }

            val editor = prefs.edit()
            if (updated.length() == 0) {
                editor.remove(PREFS_KEY_MATCHES)
            } else {
                editor.putString(PREFS_KEY_MATCHES, updated.toString())
            }
            editor.apply()
            true
        } catch (_: JSONException) {
            false
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
                isImmutable = true,
            ),
            MatchModel(
                id = 2,
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = future.timeInMillis,
                isImmutable = true,
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

    private const val PREFS_NAME = "matches_prefs"
    private const val PREFS_KEY_MATCHES = "matches"
}
