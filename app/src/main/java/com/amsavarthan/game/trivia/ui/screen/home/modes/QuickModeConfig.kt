package com.amsavarthan.game.trivia.ui.screen.home.modes

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.navigation.AppScreen
import com.amsavarthan.game.trivia.ui.navigation.createRoute
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.QuickModeUIState
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun QuickModeConfig(
    homeScreenViewModel: HomeScreenViewModel,
    gameScreenViewModel: GameScreenViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    val uiState by homeScreenViewModel.quickModeUIState.observeAsState(QuickModeUIState())

    val energy by gameScreenViewModel.energy.observeAsState(0)

    var lookingIndex by remember {
        if (uiState.selectedIndex < 0) return@remember mutableStateOf(Random.nextInt(categories.size))
        return@remember mutableStateOf(uiState.selectedIndex)
    }

    val alpha by animateFloatAsState(
        targetValue = when (uiState.selectionState) {
            QuickModeUIState.State.Running -> 1f
            QuickModeUIState.State.Finished -> 0f
        }
    )

    LaunchedEffect(uiState.selectionState) {
        if (uiState.selectionState == QuickModeUIState.State.Finished) return@LaunchedEffect

        delay(250)
        val repeatCount = Random.nextInt(5, 10)
        repeat(repeatCount) {
            lookingIndex = lookingIndex.inc().mod(categories.size)
            delay(380)
        }

        homeScreenViewModel.updateQuickModeUIState(
            QuickModeUIState(
                selectedIndex = lookingIndex,
                selectionState = QuickModeUIState.State.Finished
            )
        )
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
                targetState = lookingIndex, direction = SlideDirection.Up
            ) { index ->
                Text(text = categories[index].emoji,
                    fontSize = 96.sp,
                    modifier = Modifier.clickable(
                        enabled = uiState.selectionState == QuickModeUIState.State.Finished,
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        if (energy <= 0) {
                            navController.navigateUp()
                            Toast.makeText(context, "Insufficient Energy", Toast.LENGTH_SHORT)
                                .show()
                            return@clickable
                        }
                        navController.navigate(
                            AppScreen.CountDown.createRoute(
                                homeScreenViewModel.categoryId
                            )
                        ) {
                            launchSingleTop = true
                        }
                    })
            }

            SelectedCategoryDetail(
                index = lookingIndex,
                visible = uiState.selectionState == QuickModeUIState.State.Finished
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedCategoryDetail(index: Int, visible: Boolean) {

    val alpha by animateFloatAsState(
        targetValue = when (visible) {
            true -> 1f
            else -> 0f
        }
    )

    Column(
        modifier = Modifier.alpha(alpha),
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

            if (!forPro) return@with

            Spacer(modifier = Modifier.height(4.dp))
            Badge(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "PRO", fontSize = 14.sp)
            }

        }
    }

}