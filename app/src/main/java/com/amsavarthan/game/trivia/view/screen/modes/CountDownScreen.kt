package com.amsavarthan.game.trivia.view.screen.modes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import java.util.*
import kotlin.concurrent.schedule

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountDownScreen() {
    var count by remember { mutableStateOf(5) }

    LaunchedEffect(count) {
        if (count == 1) return@LaunchedEffect
        Timer().schedule(1000) {
            count -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            SlideOnChange(targetState = count, direction = SlideDirection.DOWN) { targetCount ->
                Text(
                    text = "$targetCount",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
