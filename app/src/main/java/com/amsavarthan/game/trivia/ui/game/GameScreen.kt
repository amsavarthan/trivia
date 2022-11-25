package com.amsavarthan.game.trivia.ui.game

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.amsavarthan.game.trivia.socket.ServerToClientSocketEvent
import com.amsavarthan.game.trivia.socket.SocketViewModel
import com.amsavarthan.game.trivia.ui.common.socket.SocketListener
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*
import kotlin.concurrent.schedule

@Destination
@Composable
fun GameScreen(
    navigator: DestinationsNavigator,
    socketViewModel: SocketViewModel,
    roomCode: String,
    viewModel: GameScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val socketId = socketViewModel.socketId

    val gameCreator = viewModel.gameCreator
    val gameStatus = viewModel.gameStatus

    val isGameCreator = socketId == gameCreator?.socketId

    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(gameStatus) {
        when (gameStatus) {
            GameStatus.CANCELLED -> {
                viewModel.leaveRoom(socketId)
                Toast.makeText(context, "Game was cancelled", Toast.LENGTH_SHORT).show()
                navigator.navigateUp()
            }
            else -> Unit
        }
    }

    DisposableEffect(roomCode) {
        viewModel.refreshGameStatus(roomCode)
        val timer = Timer().schedule(10_000L) {
            viewModel.refreshGameStatus(roomCode)
        }
        onDispose { timer.cancel() }
    }

    LaunchedEffect(isGameCreator){
        if(!isGameCreator)return@LaunchedEffect
        //TODO("Fetch question and emit to all users")
    }

    SocketListener(socketViewModel = socketViewModel) { (event, data) ->
        when (event) {
            ServerToClientSocketEvent.PlayerLeft -> viewModel.refreshGameStatus(roomCode)
            else -> Unit
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text(text = "Leave Game") },
            text = { Text(text = "Are you sure do you want to leave the game?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    viewModel.leaveRoom(socketId)
                    navigator.navigateUp()
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = "No")
                }
            })
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        Text(text = "Game screen")
    }

    BackHandler {
        showDialog = true
    }

}