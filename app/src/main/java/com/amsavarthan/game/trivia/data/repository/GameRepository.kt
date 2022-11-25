package com.amsavarthan.game.trivia.data.repository

import com.amsavarthan.game.trivia.data.api.GameAPI
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.api.response.ResponseWrapper
import com.amsavarthan.game.trivia.data.models.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

interface GameRepository {

    fun startGame(socketId: String): Flow<Resource<Boolean>>

}

class GameRepositoryImpl @Inject constructor(
    private val api: GameAPI
) : GameRepository {
    override fun startGame(socketId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val (data, message) = api.startGame(socketId)
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