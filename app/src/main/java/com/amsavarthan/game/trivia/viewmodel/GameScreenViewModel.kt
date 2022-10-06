package com.amsavarthan.game.trivia.viewmodel

import android.text.Html
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.api.interceptor.NoConnectivityException
import com.amsavarthan.game.trivia.data.models.GameResult
import com.amsavarthan.game.trivia.data.models.Question
import com.amsavarthan.game.trivia.data.models.QuestionApiResponse
import com.amsavarthan.game.trivia.data.preference.GameDatastore
import com.amsavarthan.game.trivia.data.preference.TokenDatastore
import com.amsavarthan.game.trivia.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenDatastore: TokenDatastore,
    private val gameDatastore: GameDatastore,
) : ViewModel() {

    private val _gamesPlayed = MutableLiveData(0)
    val gamesPlayed: LiveData<Int>
        get() = _gamesPlayed

    init {
        viewModelScope.launch {
            gameDatastore.gamesPlayedPreferencesFlow.collect { count ->
                _gamesPlayed.value = count
            }
        }
    }

    private val _energy = MutableLiveData(0)
    val energy: LiveData<Int>
        get() = _energy

    init {
        viewModelScope.launch {
            gameDatastore.energyPreferencesFlow.collectLatest { value ->
                _energy.value = value
            }
        }
    }

    private val _hasQuestionsLoaded = MutableLiveData(false)
    val hasQuestionsLoaded: LiveData<Boolean>
        get() = _hasQuestionsLoaded

    private val _currentQuestion = MutableLiveData<Pair<Int, Question?>>(0 to null)
    val currentQuestion: LiveData<Pair<Int, Question?>>
        get() = _currentQuestion

    private var _gameResult = mutableListOf<GameResult>()
    val gameResult get() = _gameResult.toList()

    private var questions = emptyList<Question>()
    private var currentQuestionIndex = 0

    private fun initSession() = viewModelScope.launch {
        try {
            val response = repository.getSessionToken()
            if (!response.isSuccessful) return@launch
            tokenDatastore.saveTokenToPreferencesStore(response.body()?.token ?: "")
        } catch (e: NoConnectivityException) {
            e.printStackTrace()
        }
    }

    private fun resetSession(token: String) = viewModelScope.launch {
        try {
            val response = repository.resetSessionToken(token)
            if (!response.isSuccessful) return@launch
            tokenDatastore.saveTokenToPreferencesStore(response.body()?.token ?: "")
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

        _currentQuestion.value = currentQuestionIndex to questions.getOrNull(currentQuestionIndex)
        _hasQuestionsLoaded.value = true
    }

    fun clearQuestions() {
        _hasQuestionsLoaded.value = false
        _currentQuestion.value = 0 to null
        questions = emptyList()
        currentQuestionIndex = 0
        _gameResult.clear()
    }

    fun getQuestions(categoryId: Int) {
        viewModelScope.launch {
            tokenDatastore.preferencesFlow.collectLatest { token ->
                if (token.isBlank()) initSession().join()

                try {
                    val response = repository.getQuestionsByCategory(token, categoryId)
                    if (!response.isSuccessful) return@collectLatest

                    val apiResponse = response.body()

                    when (apiResponse?.responseCode) {
                        3 -> {
                            initSession().join()
                            getQuestions(categoryId)
                        }
                        4 -> {
                            resetSession(token).join()
                            getQuestions(categoryId)
                        }
                        else -> updateData(apiResponse).join()
                    }

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

    fun increaseGamePlayCount() {
        viewModelScope.launch { gameDatastore.incrementGamePlayCount() }
    }

    fun decreaseEnergy() {
        viewModelScope.launch { gameDatastore.decreaseEnergy() }
    }

}

