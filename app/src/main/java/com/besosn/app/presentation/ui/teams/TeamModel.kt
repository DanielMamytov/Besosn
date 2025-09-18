package com.besosn.app.presentation.ui.teams

import androidx.annotation.DrawableRes
import java.io.Serializable

data class TeamModel(
    val id: Int = 0,

    val name: String,
    val city: String,
    val foundedYear: Int,
    val notes: String,
    val players: List<PlayerModel>,
    @DrawableRes val iconRes: Int = 0,
    val iconUri: String? = null,
    val isDefault: Boolean = false
) : Serializable {
    val playersCount: Int get() = players.size
}
