package com.amsavarthan.game.trivia.data.api

import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api.php")
    suspend fun getQuestionsByCategory(
        @Query("amount") count: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple",
    ): Response<QuestionApiResponse>

}