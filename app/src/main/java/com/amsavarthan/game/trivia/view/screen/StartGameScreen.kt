package com.amsavarthan.game.trivia.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.navigation.Screen
import com.amsavarthan.game.trivia.view.screen.modes.CasualModeConfig
import com.amsavarthan.game.trivia.view.screen.modes.DuelModeConfig
import com.amsavarthan.game.trivia.view.screen.modes.QuickModeConfig
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartGameScreen(onBack: () -> Unit) {
    val navController = rememberNavController()
    var destination by remember { mutableStateOf("CHOOSE MODE") }

    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route == null) return@addOnDestinationChangedListener
        destination = dest.route!!.replace("-", " ").uppercase(Locale.getDefault())
    }

    ScreenScaffold(backButton = {
        FilledTonalButton(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 24.dp, bottom = 16.dp),
            onClick = { if (!navController.navigateUp()) onBack() },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Go back")
                Text(text = destination)
            }
        }
    }) {

        NavHost(navController, startDestination = Screen.CHOOSE_MODE.route) {
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
        }

    }

    BackHandler {
        if (!navController.navigateUp()) onBack()
    }
}

@Composable
private fun ScreenScaffold(
    backButton: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            backButton()
            content()
        }
    }
}
