package com.besosn.app.domain.model

data class Match(
    val id: Int = 0,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val date: Long
)

