package com.amsavarthan.game.trivia.helper

import androidx.compose.ui.graphics.Color


object ColorGenerator {

    fun getColors(name: String): Color {
        val hue = ((360 * name.hashCode()).inv().inv()).mod(360).toFloat()
        return Color.hsl(hue, 0.7f, 0.7f)
    }
}