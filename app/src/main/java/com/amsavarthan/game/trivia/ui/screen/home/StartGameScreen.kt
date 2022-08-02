package com.amsavarthan.game.trivia.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.navigation.HomeScreens
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

    LaunchedEffect(Unit) {
        if (gameScreenViewModel.difficultyMode.isBlank()) navController.popBackStack(
            HomeScreens.CATEGORY_SCREEN.route,
            true
        )
    }

    BackHandler {
        if (!navController.navigateUp()) onBack()
    }

    ScreenScaffold(onBack = {
        if (!navController.navigateUp()) onBack()
    }) {
        NavHost(
            navController = navController,
            startDestination = HomeScreens.DIFFICULTY_SCREEN.route
        ) {
            composable(HomeScreens.DIFFICULTY_SCREEN.route) {
                ChooseDifficultyScreen(homeScreenViewModel, gameScreenViewModel, navController)
            }
            composable(HomeScreens.CATEGORY_SCREEN.route) {
                ChooseCategoryScreen(homeScreenViewModel, parentNavController)
            }
        }
    }

}


@Composable
private fun ScreenScaffold(
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BackButton(onClick = onBack)
        content()
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
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
            Text(text = "GO BACK")
        }
    }
}
