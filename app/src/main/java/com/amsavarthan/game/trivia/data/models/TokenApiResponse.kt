package com.amsavarthan.game.trivia.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenApiResponse(
    @field:Json(name = "response_code") val responseCode: Int,
    @field:Json(name = "token") val token: String
)