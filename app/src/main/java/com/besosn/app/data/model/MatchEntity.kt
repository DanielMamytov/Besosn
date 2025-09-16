package com.besosn.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val homeTeamName: String,
    val awayTeamName: String,
    val date: Long,
    val city: String,
    val notes: String,
    val homeGoals: Int?,
    val awayGoals: Int?,
    val homePhotoUri: String?,
    val awayPhotoUri: String?,
)

