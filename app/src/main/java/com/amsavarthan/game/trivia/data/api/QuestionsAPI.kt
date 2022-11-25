package com.amsavarthan.game.trivia.data.api

import com.amsavarthan.game.trivia.data.api.response.QuestionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionsAPI {

    @GET("/api.php")
    suspend fun getQuestionsByCategory(
        @Query("category") categoryId: Int,
        @Query("amount") count: Int,
        @Query("type") type: String = "multiple",
    ): QuestionsResponse

}