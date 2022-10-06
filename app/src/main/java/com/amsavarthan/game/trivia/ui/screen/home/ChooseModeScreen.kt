package com.amsavarthan.game.trivia.ui.screen.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.data.models.GameMode
import com.amsavarthan.game.trivia.data.models.gameModes
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel

@Composable
fun ChooseModeScreen(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        gameModes.forEach { item ->
            GameMode(item) {
                viewModel.resetUIStates()
                navController.navigate(item.route)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameMode(
    mode: GameMode,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(4)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape)
            .wrapContentHeight()
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