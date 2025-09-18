package com.besosn.app.presentation.ui.teams

import java.io.Serializable

data class PlayerModel(
    val fullName: String,
    val position: String,
    val number: Int,
    val photoUri: String? = null
) : Serializable
