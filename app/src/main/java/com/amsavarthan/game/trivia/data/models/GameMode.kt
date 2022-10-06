package com.amsavarthan.game.trivia.data.models

import com.amsavarthan.game.trivia.ui.navigation.HomeScreen

data class GameMode(
    val emoji: String,
    val title: String,
    val description: String,
    val route: String,
)

val gameModes = listOf(
    GameMode(
        emoji = "⚡️",
        title = "Quick Mode",
        description = "Play a game right away on category chosen randomly.",
        route = HomeScreen.QuickMode.route
    ),
    GameMode(
        emoji = "🤠",
        title = "Casual Mode",
        description = "You get to choose the category of your own choice.",
        route = HomeScreen.CasualMode.route
    ),
//    GameMode(
//        emoji = "🤝",
//        title = "Duel Mode",
//        description = "Compete and have fun playing with your friends.",
//        route = HomeScreens.DuelMode.route
//    ),
)