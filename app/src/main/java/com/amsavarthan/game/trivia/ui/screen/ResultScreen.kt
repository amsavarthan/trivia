package com.amsavarthan.game.trivia.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.screen.home.ButtonState
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.google.accompanist.insets.statusBarsPadding
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

enum class Stat {
    MISSED,
    STREAK,
    CORRECT,
    INCORRECT;
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResultScreen(
    homeScreenViewModel: HomeScreenViewModel,
    gameScreenViewModel: GameScreenViewModel,
    navController: NavController
) {

    val data = remember { gameScreenViewModel.gameResult }
    var showStats by remember { mutableStateOf(false) }
    var points by remember { mutableStateOf(0) }

    val stats = remember {
        mutableMapOf(
            Stat.STREAK to 0,
            Stat.CORRECT to 0,
            Stat.INCORRECT to 0,
            Stat.MISSED to 0
        )
    }

    LaunchedEffect(Unit) {

        homeScreenViewModel.updateButtonState(ButtonState.NORMAL)
        stats[Stat.CORRECT] = data.count { it.isCorrect }
        stats[Stat.MISSED] = data.count { it.givenAnswer.isNullOrBlank() }
        stats[Stat.INCORRECT] = data.size.minus(stats[Stat.CORRECT]!! + stats[Stat.MISSED]!!)
        stats[Stat.STREAK] = gameScreenViewModel.streak
        points =
            (stats[Stat.STREAK]!! * gameScreenViewModel.streakMultiplier) + (stats[Stat.CORRECT]!! * 5)

    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    enabled = showStats.not(),
                    onClick = { showStats = true },
                    indication = null,
                    interactionSource = MutableInteractionSource()
                )
        ) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        emitter = Emitter(
                            duration = 3,
                            TimeUnit.SECONDS
                        ).perSecond(50),
                        position = Position.Relative(0.0, 0.0)
                    ),
                    Party(
                        emitter = Emitter(
                            duration = 3,
                            TimeUnit.SECONDS
                        ).perSecond(50),
                        position = Position.Relative(1.0, 0.0)
                    )
                ),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        showStats = true
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🥳", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Congratulations!!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AnimatedVisibility(visible = showStats, enter = fadeIn()) {
                    Text(
                        text = "You get +$points Trivia Points",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Divider()
        AnimatedVisibility(
            visible = showStats,
            enter = expandVertically() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn()
        ) {
            Column {
                LazyVerticalGrid(
                    modifier = Modifier.padding(bottom = 32.dp, top = 24.dp),
                    cells = GridCells.Fixed(2)
                ) {
                    items(stats.toList()) { (title, value) ->
                        GameStat(title.name, value)
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .padding(horizontal = 32.dp),
                    onClick = { onBack(gameScreenViewModel, points, navController) }
                ) {
                    Text(text = "Continue", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }

    }

    BackHandler {
        if (showStats) onBack(gameScreenViewModel, points, navController)
    }

}

private fun onBack(
    gameScreenViewModel: GameScreenViewModel,
    points: Int,
    navController: NavController
) {
    gameScreenViewModel.finishGame(points)
    navController.navigateUp()
}

@Composable
private fun GameStat(title: String, value: Int) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.secondary
        )

    }
}
