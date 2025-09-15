package com.besosn.app.presentation.ui.teams

import java.io.Serializable

/**
 * Represents a single player inside a team.
 */
data class PlayerModel(
    val fullName: String,
    val position: String,
    val number: Int
) : Serializable
