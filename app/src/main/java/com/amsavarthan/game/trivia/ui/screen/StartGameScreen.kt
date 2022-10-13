package com.amsavarthan.game.trivia.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.game.trivia.ui.viewmodel.StartGameScreenViewModel

@Composable
fun StartGameScreen(
    startGameScreenViewModel: StartGameScreenViewModel = hiltViewModel(),
) {

    val navController = rememberNavController()
    val isResetButtonVisible by startGameScreenViewModel.isResetButtonVisible

    ScreenScaffold(topBar = {

    }) {

    }

}

@Composable
private fun ScreenScaffold(
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        topBar()
        content()
    }
}


@Composable
private fun BackButton(
    text: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Go back")
            Text(text = text)
        }
    }
}

