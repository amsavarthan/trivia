package com.amsavarthan.game.trivia.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amsavarthan.game.trivia.ui.destinations.CreateGameScreenDestination
import com.amsavarthan.game.trivia.ui.destinations.JoinGameScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.8f),
            onClick = {
                navigator.navigate(JoinGameScreenDestination())
            }) {
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Join in a game",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Button(
            modifier = Modifier
                .padding(bottom = 36.dp)
                .fillMaxWidth(0.8f),
            onClick = {
                navigator.navigate(CreateGameScreenDestination())
            }) {
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Create a new game",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

}

