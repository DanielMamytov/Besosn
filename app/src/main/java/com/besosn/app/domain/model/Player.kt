package com.besosn.app.domain.model

data class Player(
    val id: Int = 0,
    val teamId: Int,
    val name: String,
    val position: String
)

