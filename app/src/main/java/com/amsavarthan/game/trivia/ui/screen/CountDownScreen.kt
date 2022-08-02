package com.amsavarthan.game.trivia.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.navigation.Screens
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import kotlinx.coroutines.delay

enum class ResponseStatus {
    SUCCESS, FAILED, IDLE
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountDownScreen(
    viewModel: GameScreenViewModel,
    navController: NavController,
    categoryId: Int,
) {

    val context = LocalContext.current
    val status by viewModel.responseStatus.collectAsState(false)
    var count by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {

        //for countdown
        delay(900)
        while (count != 0) {
            count -= 1
            delay(1000)
        }

        viewModel.getQuestions(categoryId)

    }

    LaunchedEffect(status) {
        when (status) {
            ResponseStatus.FAILED -> {
                viewModel.clearQuestions()
                navController.navigateUp()
                Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
            ResponseStatus.SUCCESS
            -> {
                navController.navigate(Screens.GAME_SCREEN.route) {
                    launchSingleTop = true
                    popUpTo(Screens.COUNT_DOWN.route) {
                        inclusive = true
                    }
                }
            }
            else -> Unit
        }
    }

    BackHandler {
        if (count == 0) return@BackHandler
        navController.navigateUp()
    }

    AnimatedVisibility(
        visible = true,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (count) {
                0 -> CircularProgressIndicator(
                    modifier = Modifier
                        .size(150.dp)
                        .animateEnterExit()
                )
                else -> CountDown(count)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedVisibilityScope.CountDown(count: Int) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .animateEnterExit(),
        contentAlignment = Alignment.Center
    ) {
        SlideOnChange(
            targetState = count,
            direction = SlideDirection.DOWN
        ) { targetCount ->
            Text(
                text = "$targetCount",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
