package com.amsavarthan.game.trivia.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.api.interceptor.InvalidRequestException
import com.amsavarthan.game.trivia.data.api.interceptor.InvalidTokenException
import com.amsavarthan.game.trivia.data.api.interceptor.NoConnectivityException
import com.amsavarthan.game.trivia.data.api.response.Question
import com.amsavarthan.game.trivia.data.models.GameResult
import com.amsavarthan.game.trivia.data.preference.GamePreferencesRepository
import com.amsavarthan.game.trivia.data.repository.QuestionsRepository
import com.amsavarthan.game.trivia.helper.Stat
import com.amsavarthan.game.trivia.helper.StatsHelper
import com.amsavarthan.game.trivia.ui.common.QuestionsStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val questionsRepository: QuestionsRepository,
    private val gamePreferencesRepository: GamePreferencesRepository
) : ViewModel() {

    private var _questions = emptyList<Question>()

    private val _questionsStatus = mutableStateOf<QuestionsStatus>(QuestionsStatus.Idle)
    val questionsStatus: State<QuestionsStatus>
        get() = _questionsStatus

    fun getQuestions(categoryId: Int): Job {

        return viewModelScope.launch {
            kotlin.runCatching {
                questionsRepository.getQuestionsByCategory(categoryId)
            }.fold(onSuccess = { response ->
                if (response.body() == null) {
                    _questionsStatus.value = QuestionsStatus.Failed("No questions found")
                    return@launch
                }
                _questionsStatus.value = QuestionsStatus.Loaded
                _questions = response.body()!!.questions
                getNextQuestion()
            }, onFailure = { exception ->
                _questionsStatus.value = when (exception) {
                    is NoConnectivityException, is InvalidTokenException, is InvalidRequestException -> QuestionsStatus.Failed(
                        exception.message
                    )
                    else -> QuestionsStatus.Failed()
                }
            })

        }

    }

    private var _currentQuestionIndex = -1
    private val _currentQuestion = mutableStateOf(_currentQuestionIndex to (null as Question?))
    val currentQuestion: State<Pair<Int, Question?>>
        get() = _currentQuestion

    private var _gameResult = mutableListOf<GameResult>()

    fun getNextQuestion(givenAnswer: String? = null): Boolean {
        if (_currentQuestionIndex > -1) {
            val question = _questions[_currentQuestionIndex]
            _gameResult.add(GameResult(question, givenAnswer))
        }
        if (!hasNextQuestion()) return false
        loadNextQuestion()
        return true
    }

    private fun hasNextQuestion(): Boolean {
        return _currentQuestionIndex + 1 < _questions.size
    }

    private fun loadNextQuestion() {
        _currentQuestion.value = ++_currentQuestionIndex to _questions[_currentQuestionIndex]
    }

    fun getStatsForCurrentGame(): Map<Stat, Int> {
        val statsHelper = StatsHelper(_gameResult)
        return statsHelper.getStats()
    }

    fun getPointsForCurrentGame(): Int {
        val statsHelper = StatsHelper(_gameResult)
        return statsHelper.getPoints()
    }

    fun decreaseEnergy() {
        viewModelScope.launch {
            gamePreferencesRepository.decreaseEnergy()
        }
    }

    fun resetQuestionsStatus() {
        _questionsStatus.value = QuestionsStatus.Idle
    }

    fun completeGame(forced: Boolean = false) {
        clearQuestions()
        if (forced) return
        increaseGamePlayCount()
        saveGameStatsToRoom()
    }

    private fun saveGameStatsToRoom() {
        clearGameStats()
    }

    private fun clearGameStats() {
        _gameResult.clear()
    }

    private fun increaseGamePlayCount() {
        viewModelScope.launch {
            gamePreferencesRepository.increaseGamePlayCount()
        }
    }

    private fun clearQuestions() {
        _questions = emptyList()
        _currentQuestionIndex = -1
        _currentQuestion.value = _currentQuestionIndex to null
    }
}

