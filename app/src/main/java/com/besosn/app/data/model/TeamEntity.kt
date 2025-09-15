package com.besosn.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val city: String,
    val foundedYear: Int,
    val notes: String,
    val iconRes: Int,
    val isDefault: Boolean
)

