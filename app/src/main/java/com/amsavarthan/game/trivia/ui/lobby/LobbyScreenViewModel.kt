package com.amsavarthan.game.trivia.ui.lobby

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
import com.amsavarthan.game.trivia.ui.game.GameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyScreenViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val gameRepository: GameRepository
) : ViewModel() {


    var gameStatus by mutableStateOf(GameStatus.IN_LOBBY)
        private set

    var shouldStartGame by mutableStateOf<Resource<Boolean>>(Resource.Idle())
        private set

    var gameCreator by mutableStateOf<User?>(null)
        private set

    var players by mutableStateOf<List<User>?>(null)
        private set

    val refreshPlayers: (roomCode: String) -> Unit =
        debounce(delayMillis = 1000L, scope = viewModelScope) { roomCode ->
            viewModelScope.launch {
                roomRepository.getAllPlayersInRoom(roomCode).collectLatest {
                    if (it !is Resource.Success) return@collectLatest
                    players = it.data!!.users
                    gameCreator = it.data.creator
                    gameStatus = if (gameCreator != null) GameStatus.IN_LOBBY else GameStatus.CANCELLED
                }
            }
        }

    fun startGame(socketId: String) {
        viewModelScope.launch {
            gameRepository.startGame(socketId).collectLatest {
                shouldStartGame = it
            }
        }
    }

    fun leaveRoom(socketId: String) {
        viewModelScope.launch {
            roomRepository.leaveRoom(socketId).collect()
        }
    }

    fun resetGameStatus() {
        shouldStartGame = Resource.Idle()
    }

}