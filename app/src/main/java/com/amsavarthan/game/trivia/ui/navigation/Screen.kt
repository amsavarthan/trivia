package com.amsavarthan.game.trivia.ui.navigation

import java.util.*

enum class Screen(val route: String) {
    CHOOSE_MODE("choose-mode"),
    QUICK_MODE("quick-mode"),
    CASUAL_MODE("casual-mode"),
    DUEL_MODE("duel-mode"),
    COUNT_DOWN("count-down");
}

fun String.asScreen() = when {
    this == Screen.CHOOSE_MODE.route || this==Screen.CHOOSE_MODE.name() -> Screen.CHOOSE_MODE
    this == Screen.QUICK_MODE.route || this==Screen.QUICK_MODE.name()-> Screen.QUICK_MODE
    this == Screen.CASUAL_MODE.route || this==Screen.CASUAL_MODE.name()-> Screen.CASUAL_MODE
    this == Screen.DUEL_MODE.route || this==Screen.DUEL_MODE.name()-> Screen.DUEL_MODE
    this == Screen.COUNT_DOWN.route || this==Screen.COUNT_DOWN.name()-> Screen.COUNT_DOWN
    else -> throw IllegalStateException("No enum constant for given string")
}

fun Screen.name() = this.route.replace("-", " ").uppercase(Locale.getDefault())