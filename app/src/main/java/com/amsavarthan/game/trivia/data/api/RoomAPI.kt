package com.amsavarthan.game.trivia.data.api

import com.amsavarthan.game.trivia.data.api.response.ResponseWrapper
import com.amsavarthan.game.trivia.data.api.response.RoomPlayersResponse
import com.amsavarthan.game.trivia.data.models.Room
import com.amsavarthan.game.trivia.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RoomAPI {

    @GET("/room")
    suspend fun getAllPlayersInRoom(
        @Query("code") roomCode: String
    ): ResponseWrapper<RoomPlayersResponse>

    @POST("/room/create")
    suspend fun createRoom(
        @Query("id") socketId: String
    ): ResponseWrapper<Room>

    @POST("/room/join")
    suspend fun joinRoom(
        @Query("id") socketId: String,
        @Query("code") roomCode: String
    ): ResponseWrapper<Room>

    @POST("/room/leave")
    suspend fun leaveRoom(
        @Query("id") socketId: String,
    ): ResponseWrapper<Boolean>

}