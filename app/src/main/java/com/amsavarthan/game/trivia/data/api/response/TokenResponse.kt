package com.amsavarthan.game.trivia.data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @field:Json(name = "response_code") val responseCode: Int,
    @field:Json(name = "token") val token: String
)