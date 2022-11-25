package com.amsavarthan.game.trivia.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Room(
    @field:Json(name = "id") val code: Int,
    @field:Json(name = "created_by_id") val createdById: Int
)