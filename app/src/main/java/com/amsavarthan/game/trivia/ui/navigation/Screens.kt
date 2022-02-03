package com.amsavarthan.game.trivia.ui.navigation

const val ARG_CATEGORY_ID = "category_id"

enum class Screens(val route: String) {
    HOME_SCREEN("home-screen"),
    GAME_SCREEN("game-screen"),
    RESULT_SCREEN("result-screen"),
    COUNT_DOWN("count-down-screen/{${ARG_CATEGORY_ID}}");
}

fun Screens.createRoute(categoryId: Int): String = when (this) {
    Screens.COUNT_DOWN -> this.route.substringBefore('/').plus("/${categoryId}")
    else -> throw UnsupportedOperationException("Cannot create route")
}