package com.amsavarthan.game.trivia.ui.fallback

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.amsavarthan.game.trivia.socket.ServerToClientSocketEvent
import com.amsavarthan.game.trivia.socket.SocketViewModel
import com.amsavarthan.game.trivia.ui.common.socket.SocketListener
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FallbackScreen(
    navigator: DestinationsNavigator,
    socketViewModel: SocketViewModel
) {

    var canGoBack by remember {
        mutableStateOf(false)
    }

    SocketListener(socketViewModel) { (event, data) ->
        when (event) {
            ServerToClientSocketEvent.Connect -> {
                canGoBack = true
                navigator.navigateUp()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    )

    BackHandler(enabled = !canGoBack) {

    }

}