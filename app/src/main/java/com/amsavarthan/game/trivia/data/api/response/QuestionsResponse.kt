package com.amsavarthan.game.trivia.data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionsResponse(
    @field:Json(name = "response_code") val responseCode: Int,
    @field:Json(name = "results") val questions: List<Question>
)
