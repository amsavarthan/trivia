package com.amsavarthan.game.trivia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amsavarthan.game.trivia.data.models.categories
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class ButtonState {
    object Expanded : ButtonState()
    object Normal : ButtonState()
}

data class QuickModeUIState(
    val selectionState: State = State.Running,
    val selectedIndex: Int = -1
) {
    sealed class State {
        object Running : State()
        object Finished : State()
    }
}

data class CasualModeUIState(
    val selectedIndex: Int = 0
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    var categoryId: Int = categories.first().id
        private set

    private val _quickModeUIState = MutableLiveData(QuickModeUIState())
    val quickModeUIState: LiveData<QuickModeUIState>
        get() = _quickModeUIState

    fun updateQuickModeUIState(state: QuickModeUIState) {
        _quickModeUIState.value = state
        if (state.selectedIndex < 0) return
        categoryId = categories[state.selectedIndex].id
    }

    private val _casualModeUIState = MutableLiveData(CasualModeUIState())
    val casualModeUIState: LiveData<CasualModeUIState>
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



