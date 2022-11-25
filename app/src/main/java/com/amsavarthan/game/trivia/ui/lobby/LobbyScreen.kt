package com.amsavarthan.game.trivia.ui.lobby

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.data.models.User
import com.amsavarthan.game.trivia.helper.ColorGenerator
import com.amsavarthan.game.trivia.socket.ServerToClientSocketEvent
import com.amsavarthan.game.trivia.socket.SocketViewModel
import com.amsavarthan.game.trivia.ui.common.socket.SocketListener
import com.amsavarthan.game.trivia.ui.destinations.GameScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.HomeScreenDestination
import com.amsavarthan.game.trivia.ui.game.GameStatus
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import java.util.Timer
import kotlin.concurrent.schedule

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Destination
@Composable
fun GameRoomScreen(
    navigator: DestinationsNavigator,
    socketViewModel: SocketViewModel,
    roomCode: String,
    viewModel: LobbyScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val socketId = socketViewModel.socketId

    val players = viewModel.players
    val gameCreator = viewModel.gameCreator
    val gameStatus = viewModel.gameStatus
    val shouldStartGame = viewModel.shouldStartGame

    val isGameCreator = socketId == gameCreator?.socketId

    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(shouldStartGame) {
        when (shouldStartGame) {
            is Resource.Idle, is Resource.Loading -> Unit
            is Resource.Error, is Resource.Exception -> {
                Toast.makeText(context, shouldStartGame.message, Toast.LENGTH_SHORT).show()
                viewModel.resetGameStatus()
            }
            is Resource.Success -> {
                Toast.makeText(context, shouldStartGame.message, Toast.LENGTH_SHORT).show()
                viewModel.resetGameStatus()
                navigator.navigate(
                    GameScreenDestination(roomCode),
                    builder = {
                        popUpTo(HomeScreenDestination)
                    }
                )
            }
        }
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
        viewModel.refreshPlayers(roomCode)
        val timer = Timer().schedule(8_000L) {
            viewModel.refreshPlayers(roomCode)
        }
        onDispose { timer.cancel() }
    }

    SocketListener(socketViewModel = socketViewModel) { (event, data) ->
        when (event) {
            ServerToClientSocketEvent.NewPlayer,
            ServerToClientSocketEvent.PlayerLeft -> {
                viewModel.refreshPlayers(roomCode)
            }
            ServerToClientSocketEvent.StartGame -> {
                Toast.makeText(context, "Game started", Toast.LENGTH_SHORT).show()
                navigator.navigate(
                    GameScreenDestination(roomCode),
                    builder = {
                        popUpTo(HomeScreenDestination)
                    }
                )
            }
            else -> Unit
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text(text = "Leave Room") },
            text = { Text(text = "Are you sure do you want to leave the room?") },
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

    LazyVerticalGrid(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.1f))
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalArrangement = Arrangement.Center,
        columns = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(all = 24.dp)
    ) {
        if (shouldStartGame is Resource.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LinearProgressIndicator()
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp, top = 24.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Game Room",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = buildAnnotatedString {
                        append("Share the code ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(roomCode)
                        }
                        append(" with your friends.")
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                AnimatedContent(targetState = isGameCreator) { isCreator ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, CenterHorizontally)
                    ) {
                        if (isCreator) {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share game",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Button(onClick = { viewModel.startGame(socketId) }) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        6.dp,
                                        CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = "Start game",
                                    )
                                    Text(text = "Start")
                                }
                            }
                            IconButton(
                                onClick = { viewModel.refreshPlayers(roomCode) },
                                enabled = shouldStartGame is Resource.Idle
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Refresh game",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Button(onClick = { }) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        6.dp,
                                        CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Share,
                                        contentDescription = "Share game",
                                    )
                                    Text(text = "Share")
                                }
                            }
                            Button(onClick = { viewModel.refreshPlayers(roomCode) }) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        6.dp,
                                        CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Refresh game",
                                    )
                                    Text(text = "Refresh")
                                }
                            }
                        }
                    }
                }
            }
        }
        items(
            players?.sortedByDescending { player -> player.socketId == socketId }.orEmpty(),
            key = { player -> player.socketId }) { player ->
            Player(modifier = Modifier.animateItemPlacement(), player = player)
        }
    }

    BackHandler {
        showDialog = true
    }

}

@Composable
private fun Player(modifier: Modifier = Modifier, player: User) {
    var bgColor by remember {
        mutableStateOf<Color?>(null)
    }

    LaunchedEffect(player) {
        ColorGenerator.getColors(player.socketId).let { color ->
            bgColor = color
        }
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .size(100.dp)
            .background(
                bgColor?.copy(alpha = 0.7f) ?: MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = 0.7f
                ),
                CircleShape
            )
            .border(
                1.dp,
                bgColor ?: MaterialTheme.colorScheme.secondaryContainer,
                CircleShape,
            )
    )
}