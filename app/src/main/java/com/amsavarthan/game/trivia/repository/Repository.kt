package com.amsavarthan.game.trivia.repository

import com.amsavarthan.game.trivia.data.api.ApiService

class Repository(
    private val apiService: ApiService
) {

    suspend fun getQuestionsByCategory(token: String, category: Int, count: Int = 5) =
        apiService.getQuestionsByCategory(count, category, token)

    suspend fun getSessionToken() =
        apiService.getSessionToken("request")

    suspend fun resetSessionToken(token: String) =
        apiService.getSessionToken("reset", token)

}