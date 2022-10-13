package com.amsavarthan.game.trivia.ui.navigation

const val ARG_CATEGORY_ID = "category_id"

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home-screen")
    object StartGame : AppScreen("start-game-screen")
    object Game : AppScreen("game-screen")
    object Result : AppScreen("result-screen")
    object CountDown : AppScreen("count-down-screen/{${ARG_CATEGORY_ID}}")
}

fun AppScreen.CountDown.createRoute(categoryId: Int): String =
    this.route.substringBefore('/').plus("/${categoryId}")