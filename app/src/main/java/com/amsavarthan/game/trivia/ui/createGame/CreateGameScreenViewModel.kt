package com.amsavarthan.game.trivia.ui.createGame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amsavarthan.game.trivia.ui.createGame.models.*
import com.amsavarthan.game.trivia.ui.destinations.LoadingScreenDestination
import com.amsavarthan.game.trivia.ui.loading.models.LoadingScreenPurpose
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGameScreenViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(CreateGameScreenUiState())
        private set

    private val _configurations = mutableListOf<GameConfiguration>()

    fun updateSelectedIndex(index: Int) {
        uiState = uiState.copy(selectedIndex = index)
    }

    fun onBack(): Boolean {
        if (!uiState.currentGameConfiguration.hasPrevious()) return false
        if (uiState.configurationState is GameConfigurationState.Completed) {
            uiState = uiState.copy(configurationState = GameConfigurationState.InProgress)
            _configurations.removeLast()
        }
        uiState = uiState.copy(
            currentGameConfiguration = uiState.currentGameConfiguration.previous()
        )
        val lastItem = _configurations.removeLast()
        updateSelectedIndex(lastItem.index)
        return true
    }

    fun onChosen() {
        val configuration = getConfigurationData(uiState.selectedIndex)
        _configurations.add(configuration)

        if (uiState.currentGameConfiguration.hasNext()) {
            uiState = uiState.copy(
                currentGameConfiguration = uiState.currentGameConfiguration.next()
            )
            updateSelectedIndex(1)
            return
        }

        uiState = uiState.copy(
            configurationState = GameConfigurationState.Completed(_configurations)
        )
    }

    private fun getConfigurationData(index: Int): GameConfiguration {
        return GameConfiguration(
            uiState.currentGameConfiguration,
            index,
            uiState.currentGameConfiguration.items[index].id
        )
    }

    fun navigateToLoadingScreen(
        navigator: DestinationsNavigator,
        configurations: List<GameConfiguration>
    ) {
        navigator.navigate(
            LoadingScreenDestination(
                purpose = LoadingScreenPurpose.CreateRoom(configurations)
            )
        )
        resetConfigurationState()
    }

    private fun resetConfigurationState() {
        uiState = uiState.copy(configurationState = GameConfigurationState.InProgress)
    }

}