package com.amsavarthan.game.trivia.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.state.CasualModeUIState
import com.amsavarthan.game.trivia.ui.state.QuickModeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartGameScreenViewModel @Inject constructor() : ViewModel() {

    var categoryId: Int = categories.first().id
        private set

    private val _isResetButtonVisible = mutableStateOf<Boolean>(false)
    val isResetButtonVisible: State<Boolean>
        get() = _isResetButtonVisible

    fun makeResetButtonVisible(show: Boolean) {
        _isResetButtonVisible.value = show
    }

    private var _quickModeUIState = mutableStateOf(QuickModeUIState())
    val quickModeUIState: State<QuickModeUIState>
        get() = _quickModeUIState

    fun updateQuickModeUIState(state: QuickModeUIState) {
        _quickModeUIState.value = state
        if (state.selectedIndex < 0) return
        categoryId = categories[state.selectedIndex].id
    }

    private var _casualModeUIState = mutableStateOf(CasualModeUIState())
    val casualModeUIState: State<CasualModeUIState>
        get() = _casualModeUIState

    fun updateCasualModeUIState(state: CasualModeUIState) {
        _casualModeUIState.value = state
        categoryId = categories[state.selectedIndex].id
    }

    fun resetUIStates() {
        updateQuickModeUIState(QuickModeUIState())
        updateCasualModeUIState(CasualModeUIState())
    }

}