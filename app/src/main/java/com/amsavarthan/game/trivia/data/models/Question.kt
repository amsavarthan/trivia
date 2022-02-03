package com.amsavarthan.game.trivia.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Question(
    @field:Json(name = "category") var category: String,
    @field:Json(name = "type") var type: String,
    @field:Json(name = "difficulty") var difficulty: String,
    @field:Json(name = "question") var question: String,
    @field:Json(name = "correct_answer") var correctAnswer: String,
    @field:Json(name = "incorrect_answers") var incorrectAnswers: List<String>
)