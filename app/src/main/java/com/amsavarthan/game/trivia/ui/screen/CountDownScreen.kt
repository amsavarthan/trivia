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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.common.QuestionsStatus
import com.amsavarthan.game.trivia.ui.navigation.AppScreen
import com.amsavarthan.game.trivia.ui.viewmodel.CountDownScreenViewModel
import com.amsavarthan.game.trivia.ui.viewmodel.GameViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountDownScreen(
    countDownScreenViewModel: CountDownScreenViewModel = hiltViewModel(),
    gameViewModel: GameViewModel,
    navController: NavController,
    categoryId: Int,
) {

    val context = LocalContext.current
    val questionsStatus by gameViewModel.questionsStatus
    val count by countDownScreenViewModel.count

    LaunchedEffect(count) {
        if (count == 0) gameViewModel.getQuestions(categoryId)
    }

    LaunchedEffect(questionsStatus) {

        when (questionsStatus) {
            QuestionsStatus.Idle -> Unit
            is QuestionsStatus.Failed -> {
                val message = (questionsStatus as QuestionsStatus.Failed).message
                gameViewModel.resetQuestionsStatus()
                navController.navigateUp()
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            QuestionsStatus.Loaded -> {
                gameViewModel.resetQuestionsStatus()
                gameViewModel.decreaseEnergy()
                navController.navigate(AppScreen.Game.route) {
                    launchSingleTop = true
                    popUpTo(AppScreen.CountDown.route) {
                        inclusive = true
                    }
                }
            }
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
            direction = SlideDirection.Down
        ) { targetCount ->
            Text(
                text = "$targetCount",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

}
