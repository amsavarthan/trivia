package com.amsavarthan.game.trivia.ui.state

data class QuickModeUIState(
    val selectionState: State = State.Running,
    val selectedIndex: Int = -1
) {
    sealed class State {
        object Running : State()
        object Finished : State()
    }
}