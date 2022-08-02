package com.amsavarthan.game.trivia.viewmodel

import android.text.Html
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.models.GameResult
import com.amsavarthan.game.trivia.data.models.Question
import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import com.amsavarthan.game.trivia.data.preference.GameDatastore
import com.amsavarthan.game.trivia.repository.Repository
import com.amsavarthan.game.trivia.ui.screen.ResponseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val gameDatastore: GameDatastore,
) : ViewModel() {

    private val _gamesPlayed = MutableStateFlow(0)
    val gamesPlayed get() = _gamesPlayed.asStateFlow()

    init {
        viewModelScope.launch {
            gameDatastore.gamesPlayedPreferencesFlow.collectLatest { count ->
                _gamesPlayed.emit(count)
            }
        }
    }

    private val _triviaPoints = MutableStateFlow(0)
    val triviaPoints get() = _triviaPoints.asStateFlow()

    init {
        viewModelScope.launch {
            gameDatastore.triviaPointsPreferencesFlow.collectLatest { points ->
                _triviaPoints.emit(points)
            }
        }
    }

    private var _gameResult = mutableListOf<GameResult>()
    val gameResult get() = _gameResult.toList()

    private var questions = emptyList<Question>()
    private var currentQuestionIndex = 0

    private val _responseStatus = MutableStateFlow(ResponseStatus.IDLE)
    val responseStatus get() = _responseStatus.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Pair<Int, Question?>>(0 to null)
    val currentQuestion get() = _currentQuestion.asStateFlow()

    private var _difficultyMode = "";
    val difficultyMode get() = _difficultyMode

    fun updateDifficultyMode(value: String) {
        _difficultyMode = value.substringAfter("- ")
    }

    val streak: Int
        get() {
            var maxStreak = -1
            var streak = -1
            gameResult.forEach { (_, _, isCorrect) ->
                streak = if (isCorrect) streak.inc() else -1
                if (streak > maxStreak) maxStreak = streak
            }
            return if (maxStreak > 0) maxStreak else 0
        }

    val streakMultiplier
        get() = when (_difficultyMode) {
            "medium" -> 2
            "hard" -> 5
            else -> 1
        }

    private suspend fun updateData(response: QuestionApiResponse?) {
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
        _responseStatus.emit(ResponseStatus.SUCCESS)
    }

    fun getQuestions(categoryId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getQuestionsByCategory(
                    categoryId,
                    _difficultyMode
                )
                if (!response.isSuccessful) throw RuntimeException("Some error occurred : ${response.message()}")
                val apiResponse = response.body()
                updateData(apiResponse)
            } catch (e: Exception) {
                _responseStatus.emit(ResponseStatus.FAILED)
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
        val question = questions[currentQuestionIndex]
        val gameResult = GameResult(
            question,
            answer,
            isCorrect = answer != null && answer == question.correctAnswer
        )
        _gameResult.add(gameResult)
    }

    private fun String.fromHtml() = Html.fromHtml(
        this, Html.FROM_HTML_MODE_LEGACY
    ).toString()


    fun clearQuestions() {
        viewModelScope.launch {
            _responseStatus.emit(ResponseStatus.IDLE)
            _currentQuestion.emit(0 to null)
            questions = emptyList()
            currentQuestionIndex = 0
            _gameResult.clear()
        }
    }

    private fun increaseGamePlayCount() {
        viewModelScope.launch { gameDatastore.incrementGamePlayCount() }
    }

    private fun incrementTriviaPoints(points: Int) {
        viewModelScope.launch { gameDatastore.incrementTriviaPoints(points) }
    }

    fun finishGame(points: Int) {
        updateDifficultyMode("")
        increaseGamePlayCount()
        incrementTriviaPoints(points)
        clearQuestions()
    }

}

