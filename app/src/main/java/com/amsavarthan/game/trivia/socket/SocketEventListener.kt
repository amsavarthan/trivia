package com.amsavarthan.game.trivia.socket

import io.socket.emitter.Emitter

class SocketEventListener(
    private val event: ServerToClientSocketEvent,
    private val listener: Listener
) : Emitter(), Emitter.Listener {

    override fun call(data: Array<out Any?>) {
        listener.onEventCall(event, data)
    }

    interface Listener {
        fun onEventCall(event: ServerToClientSocketEvent, data: Array<out Any?>)
    }

}