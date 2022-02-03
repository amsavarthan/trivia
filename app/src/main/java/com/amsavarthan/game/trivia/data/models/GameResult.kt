package com.amsavarthan.game.trivia.data.models

data class GameResult(
    val question: Question?,
    val givenAnswer: String?,
    val isCorrect: Boolean
)
