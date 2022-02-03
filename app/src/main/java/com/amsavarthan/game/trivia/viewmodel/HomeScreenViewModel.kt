package com.amsavarthan.game.trivia.viewmodel

import androidx.lifecycle.ViewModel
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.view.screen.home.ButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    private var _categoryId = categories.first().id
    val categoryId get() = _categoryId

    private val _buttonState = MutableStateFlow(ButtonState.NORMAL)
    val buttonState get() = _buttonState.asStateFlow()

    private val _quickModeSelectedIndex = MutableStateFlow<Int?>(null)
    val quickModeSelectedIndex get() = _quickModeSelectedIndex.asStateFlow()

    private val _casualModeSelectedIndex = MutableStateFlow(0)
    val casualModeSelectedIndex get() = _casualModeSelectedIndex.asStateFlow()

    fun updateButtonState(newValue: ButtonState) {
        _buttonState.value = newValue
    }

    fun updateCasualModeSelectedIndex(newValue: Int) {
        _casualModeSelectedIndex.value = newValue
        _categoryId = categories[newValue].id
    }

    fun updateQuickModeSelectedIndex(newValue: Int) {
        _quickModeSelectedIndex.value = newValue
        _categoryId = categories[newValue].id
    }

    fun resetIndices() {
        _quickModeSelectedIndex.value = null
        _casualModeSelectedIndex.value = 0
    }

}



