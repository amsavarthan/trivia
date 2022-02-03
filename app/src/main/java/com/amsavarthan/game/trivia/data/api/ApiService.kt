package com.amsavarthan.game.trivia.data.api

import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import com.amsavarthan.game.trivia.data.models.TokenApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api.php")
    suspend fun getQuestionsByCategory(
        @Query("amount") count: Int,
        @Query("category") category: Int,
        @Query("token") token: String,
        @Query("type") type: String = "multiple",
    ): Response<QuestionApiResponse>

    @GET("/api_token.php")
    suspend fun getSessionToken(
        @Query("command") command: String,
        @Query("token") token: String? = null
    ): Response<TokenApiResponse>

}