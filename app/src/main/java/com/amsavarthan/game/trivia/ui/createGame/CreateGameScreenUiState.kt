package com.amsavarthan.game.trivia.ui.createGame

import com.amsavarthan.game.trivia.ui.createGame.models.GameConfigurationState
import com.amsavarthan.game.trivia.ui.createGame.models.GameConfigurationType

data class CreateGameScreenUiState(
    val selectedIndex: Int = 1,
    val currentGameConfiguration: GameConfigurationType = GameConfigurationType.CATEGORY,
    val configurationState: GameConfigurationState = GameConfigurationState.InProgress
)