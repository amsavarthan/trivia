package com.amsavarthan.game.trivia.data.api

import com.amsavarthan.game.trivia.data.api.response.ResponseWrapper
import retrofit2.http.POST
import retrofit2.http.Query

interface GameAPI {

    @POST("/game/start")
    suspend fun startGame(
        @Query("id") socketId: String
    ): ResponseWrapper<Boolean>

}