package com.amsavarthan.game.trivia.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.navigation.Screen
import com.amsavarthan.game.trivia.ui.navigation.asScreen
import com.amsavarthan.game.trivia.ui.navigation.name
import com.amsavarthan.game.trivia.view.screen.modes.CasualModeConfig
import com.amsavarthan.game.trivia.view.screen.modes.CountDownScreen
import com.amsavarthan.game.trivia.view.screen.modes.DuelModeConfig
import com.amsavarthan.game.trivia.view.screen.modes.QuickModeConfig
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartGameScreen(onBack: () -> Unit) {

    val navController = rememberNavController()
    var destination by remember { mutableStateOf("CHOOSE MODE") }
    var hideBackButton by remember { mutableStateOf(false) }

    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route == null) return@addOnDestinationChangedListener
        if (dest.route!!.asScreen() == Screen.COUNT_DOWN) {
            hideBackButton = true
            return@addOnDestinationChangedListener
        }
        hideBackButton = false
        destination = dest.route!!.asScreen().name()
    }

    ScreenScaffold(
        backButton = {
            val alpha by animateFloatAsState(targetValue = if (hideBackButton) 0f else 1f)
            FilledTonalButton(
                modifier = Modifier
                    .alpha(alpha)
                    .padding(top = 24.dp, bottom = 16.dp, start = 16.dp),
                onClick = { if (!navController.navigateUp()) onBack() },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Go back")
                    Text(text = destination)
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.CHOOSE_MODE.route
        ) {
            composable(Screen.CHOOSE_MODE.route) {
                ChooseModeScreen(navController)
            }
            composable(Screen.QUICK_MODE.route) {
                QuickModeConfig(navController)
            }
            composable(Screen.CASUAL_MODE.route) {
                CasualModeConfig(navController)
            }
            composable(Screen.DUEL_MODE.route) {
                DuelModeConfig(navController)
            }
            composable(Screen.COUNT_DOWN.route) {
                CountDownScreen()
            }
        }
    }

    BackHandler {
        if (!navController.navigateUp()) onBack()
    }
}

@Composable
private fun ScreenScaffold(
    backButton: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            backButton()
            content()
        }
    }
}
