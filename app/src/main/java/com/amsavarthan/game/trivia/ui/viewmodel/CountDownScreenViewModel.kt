package com.amsavarthan.game.trivia.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountDownScreenViewModel @Inject constructor() : ViewModel() {

    private val _count = mutableStateOf(3)
    val count: State<Int>
        get() = _count

    init {
        startCounter()
    }

    private fun startCounter() {
        viewModelScope.launch {
            delay(900)
            while (count.value != 0) {
                _count.value--
                delay(1000)
            }
        }
    }

}