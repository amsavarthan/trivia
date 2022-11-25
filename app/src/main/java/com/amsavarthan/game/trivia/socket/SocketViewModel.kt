package com.amsavarthan.game.trivia.socket

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.game.trivia.config.SOCKET_BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor() : ViewModel(), SocketEventListener.Listener {

    private val socket = IO.socket(SOCKET_BASE_URL)

    init {
        viewModelScope.launch {
            ServerToClientSocketEvent.getAllEvents().forEach { socketEvent ->
                socket.on(
                    socketEvent.eventName, SocketEventListener(socketEvent, this@SocketViewModel)
                )
            }
            socket.connect()
            Log.d("DEBUG", "Connecting socket..")
        }
    }

    private val isConnected: Boolean
        get() {
            if (socket.connected()) return true

            socket.connect()
            Log.d("DEBUG", "Reconnecting socket")
            return false
        }

    fun emitEvent(eventData: SocketEventData<ClientToServerSocketEvent>) {
        viewModelScope.launch {
            val (event, data) = eventData
            when (event) {
                else -> Unit
            }
        }
    }

    var onServerEvent by mutableStateOf(SocketEventData<ServerToClientSocketEvent>())
        private set

    var socketId by mutableStateOf("")
        private set

    override fun onEventCall(event: ServerToClientSocketEvent, data: Array<out Any?>) {
        viewModelScope.launch {
            onServerEvent = when (event) {
                ServerToClientSocketEvent.Disconnect,
                ServerToClientSocketEvent.Error -> SocketEventData(event)
                ServerToClientSocketEvent.Connect -> {
                    socketId = socket.id()
                    SocketEventData(event)
                }
                ServerToClientSocketEvent.NewPlayer,
                ServerToClientSocketEvent.PlayerLeft,
                ServerToClientSocketEvent.StartGame -> {
                    if (!isConnected) return@launch
                    SocketEventData(event)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
        socket.off()
        Log.d("DEBUG", "Disconnecting socket")
    }

}