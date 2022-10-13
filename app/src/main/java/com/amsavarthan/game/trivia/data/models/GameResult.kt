package com.amsavarthan.game.trivia.data.models

import com.amsavarthan.game.trivia.data.api.response.Question

data class GameResult(
    val question: Question,
    val givenAnswer: String?,
) {

    var isCorrect: Boolean
        private set

    init {
        this.isCorrect = givenAnswer != null && givenAnswer == question.correctAnswer
    }
}
