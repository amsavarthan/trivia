package com.amsavarthan.game.trivia.socket

import io.socket.client.Socket

data class SocketEventData<T : SocketEvent>(
    val event: T? = null,
    val data: String? = null
)

abstract class SocketEvent

sealed class ServerToClientSocketEvent(val eventName: String) : SocketEvent() {

    object Connect : ServerToClientSocketEvent(Socket.EVENT_CONNECT)
    object Disconnect : ServerToClientSocketEvent(Socket.EVENT_DISCONNECT)
    object Error : ServerToClientSocketEvent(Socket.EVENT_CONNECT_ERROR)

    object NewPlayer : ServerToClientSocketEvent("new_player")
    object PlayerLeft : ServerToClientSocketEvent("player_left")
    object StartGame : ServerToClientSocketEvent("start_game")

    companion object {
        fun getAllEvents() = listOf(
            Connect,
            Disconnect,
            Error,
            NewPlayer,
            PlayerLeft,
            StartGame,
        )
    }

}

sealed class ClientToServerSocketEvent(val eventName: String) : SocketEvent() {


}