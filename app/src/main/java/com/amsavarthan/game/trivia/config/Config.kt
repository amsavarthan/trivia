package com.amsavarthan.game.trivia.config

object Config {

    const val SCHEME = "https"
    const val HOST = "opentdb.com"
    const val BASE_URL = "$SCHEME://${HOST}/"


    const val MAX_ENERGY = 5

    const val STREAK_WEIGHTAGE = 10
    const val CORRECT_WEIGHTAGE = 5
}