package com.amsavarthan.game.trivia.ui.common

sealed class QuestionsStatus {
    object Idle : QuestionsStatus()
    object Loaded : QuestionsStatus()
    class Failed(val message: String? = "Some error occurred") : QuestionsStatus()
}