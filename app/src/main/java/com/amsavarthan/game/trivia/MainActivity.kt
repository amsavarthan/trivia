package com.amsavarthan.game.trivia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.navigation.ARG_CATEGORY_ID
import com.amsavarthan.game.trivia.ui.navigation.Screens
import com.amsavarthan.game.trivia.ui.screen.CountDownScreen
import com.amsavarthan.game.trivia.ui.screen.GameScreen
import com.amsavarthan.game.trivia.ui.screen.ResultScreen
import com.amsavarthan.game.trivia.ui.screen.home.HomeScreen
import com.amsavarthan.game.trivia.ui.theme.AppTheme
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    private val gameScreenViewModel: GameScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityWrapper {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screens.HOME_SCREEN.route
                ) {
                    composable(Screens.HOME_SCREEN.route) {
                        HomeScreen(homeScreenViewModel, gameScreenViewModel, navController)
                    }
                    composable(Screens.GAME_SCREEN.route) {
                        GameScreen(gameScreenViewModel, navController)
                    }
                    composable(Screens.RESULT_SCREEN.route) {
                        ResultScreen(homeScreenViewModel,gameScreenViewModel, navController)
                    }
                    composable(
                        Screens.COUNT_DOWN.route,
                        arguments = listOf(navArgument(ARG_CATEGORY_ID) { type = NavType.IntType })
                    ) {
                        val categoryId = it.arguments?.getInt(ARG_CATEGORY_ID)
                        CountDownScreen(
                            viewModel = gameScreenViewModel,
                            navController = navController,
                            categoryId = categoryId ?: categories.first().id
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ActivityWrapper(content: @Composable () -> Unit) {
    AppTheme {
        ProvideWindowInsets {
            content()
        }
    }
}
