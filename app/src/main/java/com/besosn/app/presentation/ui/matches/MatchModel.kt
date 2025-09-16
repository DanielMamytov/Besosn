package com.besosn.app.presentation.ui.matches

import androidx.annotation.DrawableRes
import java.io.Serializable

/**
 * UI model describing a single match shown on the Matches screen.
 */
data class MatchModel(
    val id: Int = 0,
    val homeTeam: String,
    val awayTeam: String,
    @DrawableRes val homeIconRes: Int = 0,
    @DrawableRes val awayIconRes: Int = 0,
    val homeIconUri: String? = null,
    val awayIconUri: String? = null,
    val date: Long,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val city: String? = null,
    val notes: String? = null,
) : Serializable {
    val isFinished: Boolean get() = homeScore != null && awayScore != null
}
