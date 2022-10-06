package com.amsavarthan.game.trivia.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.navigation.HomeScreen
import com.amsavarthan.game.trivia.ui.navigation.asScreen
import com.amsavarthan.game.trivia.ui.navigation.name
import com.amsavarthan.game.trivia.ui.screen.home.modes.CasualModeConfig
import com.amsavarthan.game.trivia.ui.screen.home.modes.QuickModeConfig
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.QuickModeUIState

@Composable
fun StartGameScreen(
    homeScreenViewModel: HomeScreenViewModel,
    gameScreenViewModel: GameScreenViewModel,
    parentNavController: NavController,
    onBack: () -> Unit
) {

    val navController = rememberNavController()
    var destination by remember { mutableStateOf(HomeScreen.ChooseMode.name()) }

    val quickModeUIState by homeScreenViewModel.quickModeUIState.observeAsState(QuickModeUIState())

    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route == null) return@addOnDestinationChangedListener
        destination = dest.route!!.asScreen().name()
    }

    ScreenScaffold(topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(destination) {
                if (navController.navigateUp()) return@BackButton
                onBack()
            }
            AnimatedVisibility(
                visible = navController.currentDestination?.route == HomeScreen.QuickMode.route && quickModeUIState.selectionState == QuickModeUIState.State.Finished
            ) {
                IconButton(onClick = {
                    homeScreenViewModel.updateQuickModeUIState(QuickModeUIState())
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Shuffle")
                }
            }
        }
    }) {
        NavHost(
            navController = navController, startDestination = HomeScreen.ChooseMode.route
        ) {
            composable(HomeScreen.ChooseMode.route) {
                ChooseModeScreen(homeScreenViewModel, navController)
            }
            composable(HomeScreen.QuickMode.route) {
                QuickModeConfig(homeScreenViewModel, gameScreenViewModel, parentNavController)
            }
            composable(HomeScreen.CasualMode.route) {
                CasualModeConfig(homeScreenViewModel, gameScreenViewModel, parentNavController)
            }
//            composable(HomeScreens.DuelMode.route) {
//                DuelModeConfig(parentNavController)
//            }
        }
    }

    BackHandler {
        if (!navController.navigateUp()) onBack()
    }
}

@Composable
private fun BackButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Go back")
            Text(text = text)
        }
    }
}

@Composable
private fun ScreenScaffold(
    topBar: @Composable () -> Unit, content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        topBar()
        content()
    }
}
