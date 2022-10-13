package com.amsavarthan.game.trivia.data.repository

import com.amsavarthan.game.trivia.data.api.QuestionsAPI
import com.amsavarthan.game.trivia.data.api.response.QuestionsResponse
import retrofit2.Response

interface QuestionsRepository {
    suspend fun getQuestionsByCategory(
        categoryId: Int,
        count: Int = 2
    ): Response<QuestionsResponse>
}

class QuestionsRepositoryImpl(
    private val questionAPI: QuestionsAPI
) : QuestionsRepository {

    override suspend fun getQuestionsByCategory(
        categoryId: Int,
        count: Int
    ) = questionAPI.getQuestionsByCategory(categoryId, count)

}