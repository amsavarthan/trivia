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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.navigation.ARG_CATEGORY_ID
import com.amsavarthan.game.trivia.ui.navigation.AppScreen
import com.amsavarthan.game.trivia.ui.screen.CountDownScreen
import com.amsavarthan.game.trivia.ui.screen.GameScreen
import com.amsavarthan.game.trivia.ui.screen.ResultScreen
import com.amsavarthan.game.trivia.ui.screen.home.HomeScreen
import com.amsavarthan.game.trivia.ui.theme.AppTheme
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.amsavarthan.game.trivia.worker.EnergyRefillWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

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
                    startDestination = AppScreen.Home.route
                ) {
                    composable(AppScreen.Home.route) {
                        HomeScreen(homeScreenViewModel, gameScreenViewModel, navController)
                    }
                    composable(AppScreen.Game.route) {
                        GameScreen(gameScreenViewModel, navController)
                    }
                    composable(AppScreen.Result.route) {
                        ResultScreen(gameScreenViewModel, navController)
                    }
                    composable(
                        AppScreen.CountDown.route,
                        arguments = listOf(navArgument(ARG_CATEGORY_ID) { type = NavType.IntType })
                    ) {
                        val categoryId =
                            it.arguments?.getInt(ARG_CATEGORY_ID) ?: categories.first().id
                        CountDownScreen(gameScreenViewModel, navController, categoryId)
                    }
                }
            }
        }
        initWorker()
    }

    private fun initWorker() {
        val request = PeriodicWorkRequestBuilder<EnergyRefillWorker>(15, TimeUnit.MINUTES).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            "energy-refill-task",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

}

@Composable
fun ActivityWrapper(content: @Composable () -> Unit) {
    AppTheme {
        content()
    }
}
