package com.besosn.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamId: Int,
    val fullName: String,
    val position: String,
    val number: Int
)

