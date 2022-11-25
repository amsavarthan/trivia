package com.amsavarthan.game.trivia.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.models.User
import com.amsavarthan.game.trivia.data.repository.GameRepository
import com.amsavarthan.game.trivia.data.repository.RoomRepository
import com.amsavarthan.game.trivia.helper.debounce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class GameStatus() {
    ON_GOING, CANCELLED, IN_LOBBY
}


@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    var gameStatus by mutableStateOf(GameStatus.ON_GOING)
        private set

    var gameCreator by mutableStateOf<User?>(null)
        private set

    fun refreshGameStatus(roomCode: String) {
        viewModelScope.launch {
            roomRepository.getAllPlayersInRoom(roomCode).collectLatest {
                if (it !is Resource.Success) return@collectLatest
                val players = it.data!!.users
                gameCreator = it.data.creator
                gameStatus = if (players.size > 1 && gameCreator != null) {
                    GameStatus.ON_GOING
                } else {
                    GameStatus.CANCELLED
                }
            }
        }
    }

    fun leaveRoom(socketId: String) {
        viewModelScope.launch {
            roomRepository.leaveRoom(socketId).collect()
        }
    }
}