package com.amsavarthan.game.trivia.data.repository

import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.api.RoomAPI
import com.amsavarthan.game.trivia.data.api.response.ResponseWrapper
import com.amsavarthan.game.trivia.data.api.response.RoomPlayersResponse
import com.amsavarthan.game.trivia.data.models.Room
import com.amsavarthan.game.trivia.data.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

interface RoomRepository {

    suspend fun createRoom(socketId: String): Flow<Resource<Room>>
    suspend fun joinRoom(socketId: String, roomCode: String): Flow<Resource<Room>>
    suspend fun leaveRoom(socketId: String): Flow<Resource<Boolean>>

    suspend fun getAllPlayersInRoom(roomCode: String): Flow<Resource<RoomPlayersResponse>>

}

class RoomRepositoryImpl @Inject constructor(
    private val api: RoomAPI
) : RoomRepository {

    override suspend fun getAllPlayersInRoom(roomCode: String): Flow<Resource<RoomPlayersResponse>> =
        flow {
            emit(Resource.Loading())
            try {
                val (data, message) = api.getAllPlayersInRoom(roomCode)
                emit(Resource.Success(data!!, message))
            } catch (e: HttpException) {
                val body = e.response()?.errorBody() ?: throw Exception("Some error occurred")
                val moshi = Moshi.Builder().build()
                val type = Types.newParameterizedType(
                    ResponseWrapper::class.java,
                    RoomPlayersResponse::class.java
                )
                val errorResponse = moshi.adapter<ResponseWrapper<RoomPlayersResponse>>(type)
                    .fromJson(body.string()) ?: throw Exception("Some error occurred")
                emit(Resource.Error(errorResponse.message))
            } catch (e: Exception) {
                emit(Resource.Exception(e.message.toString()))
            }
        }

    override suspend fun createRoom(socketId: String): Flow<Resource<Room>> = flow {
        emit(Resource.Loading())
        try {
            val (data, message) = api.createRoom(socketId)
            emit(Resource.Success(data!!, message))
        } catch (e: HttpException) {
            val body = e.response()?.errorBody() ?: throw Exception("Some error occurred")
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(ResponseWrapper::class.java, Room::class.java)
            val errorResponse = moshi.adapter<ResponseWrapper<Room>>(type)
                .fromJson(body.string()) ?: throw Exception("Some error occurred")
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Exception(e.message.toString()))
        }
    }

    override suspend fun joinRoom(socketId: String, roomCode: String): Flow<Resource<Room>> = flow {
        emit(Resource.Loading())
        try {
            val (data, message) = api.joinRoom(socketId, roomCode)
            emit(Resource.Success(data!!, message))
        } catch (e: HttpException) {
            val body = e.response()?.errorBody() ?: throw Exception("Some error occurred")
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(ResponseWrapper::class.java, Room::class.java)
            val errorResponse = moshi.adapter<ResponseWrapper<Room>>(type)
                .fromJson(body.string()) ?: throw Exception("Some error occurred")
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Exception(e.message.toString()))
        }
    }

    override suspend fun leaveRoom(socketId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val (data, message) = api.leaveRoom(socketId)
            emit(Resource.Success(data!!, message))
        } catch (e: HttpException) {
            val body = e.response()?.errorBody() ?: throw Exception("Some error occurred")
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(ResponseWrapper::class.java, Boolean::class.javaObjectType)
            val errorResponse = moshi.adapter<ResponseWrapper<Boolean>>(type)
                .fromJson(body.string()) ?: throw Exception("Some error occurred")
            emit(Resource.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Resource.Exception(e.message.toString()))
        }
    }

}