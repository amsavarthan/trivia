package com.amsavarthan.game.trivia.viewmodel

import android.text.Html
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.models.GameResult
import com.amsavarthan.game.trivia.data.models.Question
import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import com.amsavarthan.game.trivia.data.preference.TokenDatastore
import com.amsavarthan.game.trivia.interceptor.NoConnectivityException
import com.amsavarthan.game.trivia.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val datastore: TokenDatastore
) : ViewModel() {

    private var _gameResult = mutableListOf<GameResult>()
    val gameResult get() = _gameResult.toList()

    private var questions = emptyList<Question>()
    private var currentQuestionIndex = 0

    private val _hasQuestionsLoaded = MutableStateFlow(false)
    val hasQuestionsLoaded get() = _hasQuestionsLoaded.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Pair<Int, Question?>>(0 to null)
    val currentQuestion get() = _currentQuestion.asStateFlow()

    private fun initSession() = viewModelScope.launch {
        try {
            val response = repository.getSessionToken()
            if (!response.isSuccessful) return@launch
            datastore.saveTokenToPreferencesStore(response.body()?.token ?: "")
        } catch (e: NoConnectivityException) {
            e.printStackTrace()
        }
    }

    private fun resetSession(token: String) = viewModelScope.launch {
        try {
            val response = repository.resetSessionToken(token)
            if (!response.isSuccessful) return@launch
            datastore.saveTokenToPreferencesStore(response.body()?.token ?: "")
        } catch (e: NoConnectivityException) {
            e.printStackTrace()
        }
    }

    private fun updateData(response: QuestionApiResponse?) = viewModelScope.launch {
        questions = response?.questions.orEmpty()
        questions.forEach {
            it.run {
                question = question.fromHtml()
                incorrectAnswers = incorrectAnswers.map { incorrectAnswer ->
                    incorrectAnswer.fromHtml()
                }
            }
        }

        _currentQuestion.emit(currentQuestionIndex to questions.getOrNull(currentQuestionIndex))
        _hasQuestionsLoaded.emit(true)
    }

    fun getQuestions(categoryId: Int) {
        viewModelScope.launch {
            datastore.preferencesFlow.collectLatest { token ->
                if (token.isBlank()) initSession().join()

                try {
                    val response = repository.getQuestionsByCategory(token, categoryId)
                    if (!response.isSuccessful) return@collectLatest

                    val apiResponse = response.body()
                    if (apiResponse?.responseCode == 4) resetSession(token).join()
                    updateData(apiResponse).join()
                } catch (e: NoConnectivityException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun nextQuestion(): Boolean {
        currentQuestionIndex++
        if (currentQuestionIndex >= questions.size) return false
        _currentQuestion.value = currentQuestionIndex to questions[currentQuestionIndex]
        return true
    }

    fun calculateScore(answer: String?) {
        viewModelScope.launch {
            currentQuestion.collectLatest { (_, question) ->
                _gameResult.add(
                    GameResult(
                        question,
                        answer,
                        isCorrect = answer != null && answer == question?.correctAnswer
                    )
                )
            }
        }
    }

    private fun String.fromHtml() = Html.fromHtml(
        this, Html.FROM_HTML_MODE_LEGACY
    ).toString()

}

