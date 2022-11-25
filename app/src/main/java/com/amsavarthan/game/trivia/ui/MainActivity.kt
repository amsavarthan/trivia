package com.amsavarthan.game.trivia.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.socket.ServerToClientSocketEvent
import com.amsavarthan.game.trivia.socket.SocketViewModel
import com.amsavarthan.game.trivia.ui.common.socket.SocketListener
import com.amsavarthan.game.trivia.ui.destinations.FallbackScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.GameRoomScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.GameScreenDestination
import com.amsavarthan.game.trivia.ui.theme.AppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {

                val socketViewModel = hiltViewModel<SocketViewModel>()
                val navController = rememberNavController()

                SocketListener(socketViewModel) { (event, data) ->
                    when (event) {
                        ServerToClientSocketEvent.Disconnect,
                        ServerToClientSocketEvent.Error -> {
                            if (navController.currentDestination?.route == FallbackScreenDestination.route) return@SocketListener
                            navController.navigate(FallbackScreenDestination())
                        }
                        else -> Unit
                    }
                }

                LaunchedEffect(Unit){
                    delay(500)
                    navController.navigate(GameScreenDestination("5141"))
                }


                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController,
                    dependenciesContainerBuilder = {
                        dependency(socketViewModel)
                    })
            }
        }
    }

}