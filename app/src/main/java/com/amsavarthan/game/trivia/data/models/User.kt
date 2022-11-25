package com.amsavarthan.game.trivia.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "socket_id") val socketId: String,
    @field:Json(name = "room_id") val roomId: Int
)