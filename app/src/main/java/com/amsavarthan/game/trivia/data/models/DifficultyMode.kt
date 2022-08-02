package com.amsavarthan.game.trivia.data.models

data class DifficultyMode(
    val emoji: String,
    val title: String,
    val description: String,
)

val difficultyModes = listOf(
    DifficultyMode(
        emoji = "\uD83E\uDD14",
        title = "Novice - Easy",
        description = "The question will be easier to answer."
    ),
    DifficultyMode(
        emoji = "🤠",
        title = "Competent - Medium",
        description = "You will be provided questions that challenges you"
    ),
    DifficultyMode(
        emoji = "\uD83D\uDE0E",
        title = "Expert - Hard",
        description = "Try some of our hard questions"
    ),
)