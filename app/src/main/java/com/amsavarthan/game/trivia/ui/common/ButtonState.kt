package com.amsavarthan.game.trivia.ui.common

sealed class ButtonState {
    object Expanded : ButtonState()
    object Normal : ButtonState()
}