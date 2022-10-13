package com.amsavarthan.game.trivia.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.config.Config
import com.amsavarthan.game.trivia.data.preference.GamePreferencesRepository
import com.amsavarthan.game.trivia.ui.common.ButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val gamePreferencesRepository: GamePreferencesRepository,
) : ViewModel() {

    private var _buttonState = mutableStateOf<ButtonState>(ButtonState.Normal)
    val buttonState: State<ButtonState>
        get() = _buttonState

    private val _gamesPlayed = mutableStateOf<Int>(0)
    val gamesPlayed: State<Int>
        get() = _gamesPlayed

    private val _energy = mutableStateOf<Int>(Config.MAX_ENERGY)
    val energy: State<Int>
        get() = _energy

    init {
        viewModelScope.launch {
            gamePreferencesRepository.preferencesFlow.collect { prefs ->
                _gamesPlayed.value = prefs.gamesPlayed
                _energy.value = prefs.energy
            }
        }
    }

    fun updateButtonState(state: ButtonState) {
        _buttonState.value = state
    }

}