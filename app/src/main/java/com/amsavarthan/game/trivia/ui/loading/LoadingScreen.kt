package com.amsavarthan.game.trivia.ui.loading

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amsavarthan.game.trivia.data.api.Resource
import com.amsavarthan.game.trivia.socket.SocketViewModel
import com.amsavarthan.game.trivia.ui.destinations.GameRoomScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.HomeScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.JoinGameScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.LoadingScreenDestination
import com.amsavarthan.game.trivia.ui.loading.models.LoadingScreenPurpose
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@Destination
@Composable
fun LoadingScreen(
    navigator: DestinationsNavigator,
    socketViewModel: SocketViewModel,
    purpose: LoadingScreenPurpose,
    viewModel: LoadingScreenViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val status = viewModel.status
    val socketId = socketViewModel.socketId

    val statusText by remember {
        mutableStateOf(
            when (purpose) {
                is LoadingScreenPurpose.CreateRoom -> "Creating a room..."
                is LoadingScreenPurpose.JoinRoom -> "Joining the room..."
            }
        )
    }

    LaunchedEffect(purpose) {
        when (purpose) {
            is LoadingScreenPurpose.CreateRoom -> viewModel.createRoom(socketId)
            is LoadingScreenPurpose.JoinRoom -> viewModel.joinRoom(socketId, purpose.gameCode)
        }
    }

    LaunchedEffect(status) {
        when (status) {
            is Resource.Idle, is Resource.Loading -> Unit
            is Resource.Exception, is Resource.Error -> {
                Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                navigator.navigateUp()
                viewModel.resetStatus()
            }
            is Resource.Success -> {
                Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                navigator.navigate(
                    GameRoomScreenDestination(roomCode = status.data!!.code.toString()),
                    builder = {
                        popUpTo(HomeScreenDestination)
                    }
                )
                viewModel.resetStatus()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.1f))
            .fillMaxSize()
            .systemBarsPadding()
            .padding(vertical = 48.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxSize(),
            )
        }
    }

}

