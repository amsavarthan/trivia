package com.amsavarthan.game.trivia.data.api.response

import com.amsavarthan.game.trivia.data.models.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomPlayersResponse(
    @field:Json(name = "users") val users: List<User> = emptyList(),
    @field:Json(name = "creator") val creator: User? = null
)