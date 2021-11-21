package com.amsavarthan.game.trivia.view.screen.modes

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuickModeConfig(navController: NavController) {

    var lookingIndex by remember { mutableStateOf(Random.nextInt(categories.size)) }
    var selectionStopped by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = when (selectionStopped) {
            true -> 0f
            else -> 1f
        }
    )

    LaunchedEffect(false) {
        selectionStopped = false
        delay(200)
        val repeatCount = Random.nextInt(5, 11)
        repeat(repeatCount) {
            lookingIndex = lookingIndex.inc().mod(categories.size)
            delay(250)
        }
        selectionStopped = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            SlideOnChange(
                targetState = lookingIndex,
                direction = SlideDirection.UP
            ) { index ->
                Text(text = categories[index].emoji,
                    fontSize = 96.sp,
                    modifier = Modifier
                        .clickable(
                            enabled = selectionStopped,
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            navController.navigate(Screen.COUNT_DOWN.route) {
                                launchSingleTop = true
                            }
                        }
                )
            }

            SelectedCategoryDetail(index = lookingIndex, visible = selectionStopped)

        }

        Box {
            Text(
                modifier = Modifier
                    .padding(24.dp)
                    .alpha(alpha),
                text = "We are choosing a category for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SelectedCategoryDetail(index: Int, visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { height -> height },
        exit = fadeOut() + slideOutVertically { height -> -height }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            with(categories[index]) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = name, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Click on the emoji to start the game",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                if (forPro) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(
                        modifier = Modifier
                            .padding(8.dp)
                            .animateEnterExit(
                                enter = fadeIn(),
                                exit = fadeOut()
                            )
                    ) {
                        Text(text = "PRO", fontSize = 14.sp)
                    }
                }

            }
        }
    }
}