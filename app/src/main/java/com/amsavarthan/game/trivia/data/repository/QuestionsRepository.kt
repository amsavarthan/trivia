package com.amsavarthan.game.trivia.data.repository

import com.amsavarthan.game.trivia.data.api.QuestionsAPI
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.api.response.QuestionsResponse
import com.amsavarthan.game.trivia.data.api.response.ResponseWrapper
import com.amsavarthan.game.trivia.data.models.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import retrofit2.Response

interface QuestionsRepository {
    suspend fun getQuestionsByCategory(
        categoryId: Int,
        count: Int = 2
    ): Flow<Resource<QuestionsResponse>>
}

class QuestionsRepositoryImpl(
    private val api: QuestionsAPI
) : QuestionsRepository {

    override suspend fun getQuestionsByCategory(
        categoryId: Int,
        count: Int
    ): Flow<Resource<QuestionsResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getQuestionsByCategory(categoryId, count)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Exception(e.message.toString()))
        }
    }


}