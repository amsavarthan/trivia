package com.amsavarthan.game.trivia.ui.navigation

import java.util.*

enum class HomeScreens(val route: String) {
    CHOOSE_MODE("choose-mode"),
    QUICK_MODE("quick-mode"),
    CASUAL_MODE("casual-mode"),
    DUEL_MODE("duel-mode"),
}

fun String.asScreen() = when {
    this == HomeScreens.CHOOSE_MODE.route || this == HomeScreens.CHOOSE_MODE.name() -> HomeScreens.CHOOSE_MODE
    this == HomeScreens.QUICK_MODE.route || this == HomeScreens.QUICK_MODE.name() -> HomeScreens.QUICK_MODE
    this == HomeScreens.CASUAL_MODE.route || this == HomeScreens.CASUAL_MODE.name() -> HomeScreens.CASUAL_MODE
    this == HomeScreens.DUEL_MODE.route || this == HomeScreens.DUEL_MODE.name() -> HomeScreens.DUEL_MODE
    else -> throw IllegalStateException("No enum constant for given string")
}

fun HomeScreens.name() = this.route.replace("-", " ").uppercase(Locale.getDefault())