package com.amsavarthan.game.trivia.view.screen

import androidx.compose.runtime.*
import com.amsavarthan.game.trivia.data.models.GameResult
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(viewModel: GameScreenViewModel) {

    var data = remember { viewModel.gameResult }
    var correctAnswers by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        if (data.isEmpty()) {
            data = buildList {
                add(GameResult(null, "", true))
                add(GameResult(null, "", true))
                add(GameResult(null, "", false))
                add(GameResult(null, "", true))
                add(GameResult(null, "", false))
                add(GameResult(null, "", true))
            }
        }

        val _correctAnswers = data.count { it.isCorrect }
        while (correctAnswers != _correctAnswers) {
            correctAnswers++
            delay(50)
        }

    }


}