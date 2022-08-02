package com.amsavarthan.game.trivia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.screen.home.ButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    private var _categoryId = categories.first().id

    val categoryId get() = _categoryId

    private val _buttonState = MutableStateFlow(ButtonState.NORMAL)
    val buttonState get() = _buttonState.asStateFlow()

    fun updateButtonState(newValue: ButtonState) {
        if (buttonState.value == newValue) return
        viewModelScope.launch {
            _buttonState.emit(newValue)
        }
    }

    private val _casualModeSelectedIndex = MutableStateFlow(0)
    val casualModeSelectedIndex get() = _casualModeSelectedIndex.asStateFlow()

    fun updateCasualModeSelectedIndex(newValue: Int) {
        viewModelScope.launch {
            _casualModeSelectedIndex.emit(newValue)
            _categoryId = categories[newValue].id
        }
    }

}



