package com.amsavarthan.game.trivia.ui.loading

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.models.Room
import com.amsavarthan.game.trivia.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoadingScreenViewModel @Inject constructor(
    private val repository: RoomRepository,
) : ViewModel() {

    var status by mutableStateOf<Resource<Room>>(Resource.Idle())
        private set

    fun createRoom(socketId: String) {
        viewModelScope.launch {
            repository.createRoom(socketId).collectLatest {
                status = it
            }
        }
    }

    fun joinRoom(socketId: String, roomCode: String) {
        viewModelScope.launch {
            repository.joinRoom(socketId, roomCode).collectLatest {
                status = it
            }
        }
    }

    fun resetStatus() {
        status = Resource.Idle()
    }

}