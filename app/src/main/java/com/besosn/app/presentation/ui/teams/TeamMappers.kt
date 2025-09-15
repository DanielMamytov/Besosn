package com.besosn.app.presentation.ui.teams

import com.besosn.app.data.model.PlayerEntity
import com.besosn.app.data.model.TeamEntity

fun TeamEntity.toModel(players: List<PlayerEntity>): TeamModel = TeamModel(
    id = id,
    name = name,
    city = city,
    foundedYear = foundedYear,
    notes = notes,
    players = players.map { it.toModel() },
    iconRes = iconRes,
    iconUri = iconUri,
    isDefault = isDefault
)

fun PlayerEntity.toModel(): PlayerModel = PlayerModel(
    fullName = fullName,
    position = position,
    number = number
)

fun TeamModel.toEntity(): TeamEntity = TeamEntity(
    id = id,
    name = name,
    city = city,
    foundedYear = foundedYear,
    notes = notes,
    iconRes = iconRes,
    iconUri = iconUri,
    isDefault = isDefault
)

fun PlayerModel.toEntity(teamId: Int): PlayerEntity = PlayerEntity(
    teamId = teamId,
    fullName = fullName,
    position = position,
    number = number
)
