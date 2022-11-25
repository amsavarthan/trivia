package com.amsavarthan.game.trivia.data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWrapper<T>(
    @field:Json(name = "data") val data: T?,
    @field:Json(name = "message") val message: String
)