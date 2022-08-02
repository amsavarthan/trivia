package com.amsavarthan.game.trivia.repository

import com.amsavarthan.game.trivia.data.api.ApiService
import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import retrofit2.Response

class Repository(
    private val apiService: ApiService
) {

    suspend fun getQuestionsByCategory(
        category: Int,
        difficulty: String,
        count: Int = 10
    ): Response<QuestionApiResponse> {
        return apiService.getQuestionsByCategory(count, category, difficulty)
    }

}