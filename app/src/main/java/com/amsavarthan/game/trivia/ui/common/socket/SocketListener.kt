package com.amsavarthan.game.trivia.ui.common.socket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.amsavarthan.game.trivia.socket.ServerToClientSocketEvent
import com.amsavarthan.game.trivia.socket.SocketEventData
import com.amsavarthan.game.trivia.socket.SocketViewModel

@Composable
fun SocketListener(
    socketViewModel: SocketViewModel,
    onEvent: (SocketEventData<ServerToClientSocketEvent>) -> Unit
) {

    val currentOnEvent by rememberUpdatedState(newValue = onEvent)
    val onServerEvent = socketViewModel.onServerEvent
    LaunchedEffect(onServerEvent.event) {
        currentOnEvent(onServerEvent)
    }

}