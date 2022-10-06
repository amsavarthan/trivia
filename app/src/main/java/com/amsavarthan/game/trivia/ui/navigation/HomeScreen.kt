package com.amsavarthan.game.trivia.ui.navigation

import java.util.*

sealed class HomeScreen(val route: String) {
    object ChooseMode : HomeScreen("choose-mode")
    object QuickMode : HomeScreen("quick-mode")
    object CasualMode : HomeScreen("casual-mode")
    object DuelMode : HomeScreen("duel-mode")
}

fun String.asScreen() = when {
    this == HomeScreen.ChooseMode.route || this == HomeScreen.ChooseMode.name() -> HomeScreen.ChooseMode
    this == HomeScreen.QuickMode.route || this == HomeScreen.QuickMode.name() -> HomeScreen.QuickMode
    this == HomeScreen.CasualMode.route || this == HomeScreen.CasualMode.name() -> HomeScreen.CasualMode
    this == HomeScreen.DuelMode.route || this == HomeScreen.DuelMode.name() -> HomeScreen.DuelMode
    else -> throw IllegalStateException("No enum constant for given string")
}

fun HomeScreen.name() = this.route.replace("-", " ").uppercase(Locale.getDefault())