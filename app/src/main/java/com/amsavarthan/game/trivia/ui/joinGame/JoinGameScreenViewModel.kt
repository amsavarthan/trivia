package com.amsavarthan.game.trivia.ui.joinGame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amsavarthan.game.trivia.ui.destinations.LoadingScreenDestination
import com.amsavarthan.game.trivia.ui.loading.models.LoadingScreenPurpose
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JoinGameScreenViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(JoinGameScreenUiState())
        private set

    val isGameCodeFilled: Boolean
        get() = uiState.gameCode.length == uiState.digitCount

    fun changeGameCode(code: String) {
        uiState = uiState.copy(gameCode = code)
    }

    fun navigateToLoadingScreen(navigator: DestinationsNavigator, gameCode: String) {
        navigator.navigate(LoadingScreenDestination(purpose = LoadingScreenPurpose.JoinRoom(gameCode)))
    }

}