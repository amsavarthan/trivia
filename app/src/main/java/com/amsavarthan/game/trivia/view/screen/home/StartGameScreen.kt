package com.amsavarthan.game.trivia.view.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.navigation.HomeScreens
import com.amsavarthan.game.trivia.ui.navigation.asScreen
import com.amsavarthan.game.trivia.ui.navigation.name
import com.amsavarthan.game.trivia.view.screen.home.modes.CasualModeConfig
import com.amsavarthan.game.trivia.view.screen.home.modes.DuelModeConfig
import com.amsavarthan.game.trivia.view.screen.home.modes.QuickModeConfig
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel

@Composable
fun StartGameScreen(
    homeScreenViewModel: HomeScreenViewModel,
    gameScreenViewModel: GameScreenViewModel,
    parentNavController: NavController,
    onBack: () -> Unit
) {

    val navController = rememberNavController()
    var destination by remember { mutableStateOf(HomeScreens.CHOOSE_MODE.name()) }

    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route == null) return@addOnDestinationChangedListener
        destination = dest.route!!.asScreen().name()
    }

    ScreenScaffold(
        backButton = {
            BackButton(destination) {
                if (!navController.navigateUp()) onBack()
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeScreens.CHOOSE_MODE.route
        ) {
            composable(HomeScreens.CHOOSE_MODE.route) {
                ChooseModeScreen(homeScreenViewModel, navController)
            }
            composable(HomeScreens.QUICK_MODE.route) {
                QuickModeConfig(homeScreenViewModel, gameScreenViewModel, parentNavController)
            }
            composable(HomeScreens.CASUAL_MODE.route) {
                CasualModeConfig(homeScreenViewModel, gameScreenViewModel, parentNavController)
            }
            composable(HomeScreens.DUEL_MODE.route) {
                DuelModeConfig(parentNavController)
            }
        }
    }

    BackHandler {
        if (!navController.navigateUp()) onBack()
    }
}

@Composable
private fun BackButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        modifier = Modifier.padding(
            top = 24.dp,
            bottom = 16.dp,
            start = 16.dp
        ),
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
    backButton: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        backButton()
        content()
    }
}
