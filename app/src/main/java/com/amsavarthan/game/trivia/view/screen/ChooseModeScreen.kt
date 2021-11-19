package com.amsavarthan.game.trivia.view.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.navigation.Screen

private data class GameMode(
    val emoji: String,
    val title: String,
    val description: String,
    val route: String,
)

private val gameModes = listOf(
    GameMode(
        emoji = "⚡️",
        title = "Quick Mode",
        description = "Play a game right away on category chosen randomly.",
        route = Screen.QUICK_MODE.route
    ),
    GameMode(
        emoji = "🤠",
        title = "Casual Mode",
        description = "You get to choose the category of your own choice.",
        route = Screen.CASUAL_MODE.route
    ),
    GameMode(
        emoji = "🤝",
        title = "Duel Mode",
        description = "Compete and have fun playing with your friends.",
        route = Screen.DUEL_MODE.route
    ),
)

@Composable
fun ChooseModeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        gameModes.forEach { item ->
            GameMode(item) {
                navController.navigate(item.route)
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GameMode(mode: GameMode, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        indication = rememberRipple(),
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape)
            .wrapContentHeight()
            .widthIn(max = 400.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape
            ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = mode.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = mode.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                text = mode.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}