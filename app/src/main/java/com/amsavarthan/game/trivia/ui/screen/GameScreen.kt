package com.amsavarthan.game.trivia.ui.screen.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.navigation.Screens
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel,
    navController: NavController
) {

    val data by viewModel.currentQuestion.collectAsState()
    val (questionNumber, questionData) = data

    var showDialog by remember { mutableStateOf(false) }
    var selectedAnswerIndex by remember { mutableStateOf(-1) }
    var answers by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(questionData) {
        if (questionData == null) return@LaunchedEffect
        selectedAnswerIndex = -1
        answers = buildList {
            addAll(questionData.incorrectAnswers)
            add(questionData.correctAnswer)
        }.shuffled()
    }

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .padding(all = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Timer(
                viewModel = viewModel,
                onTimeout = {
                    viewModel.calculateScore(answers.getOrNull(selectedAnswerIndex))
                },
                onGameComplete = {
                    navController.navigate(Screens.RESULT_SCREEN.route) {
                        launchSingleTop = true
                        popUpTo(Screens.GAME_SCREEN.route) {
                            inclusive = true
                        }
                    }
                }
            )
            AnimatedVisibility(visible = questionData != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    questionData?.let { questionData ->
                        Text(
                            text = "QUESTION ${questionNumber + 1}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 2.sp
                            ),
                        )
                        SlideOnChange(
                            targetState = questionNumber,
                            direction = SlideDirection.ADAPTIVE
                        ) {
                            Text(
                                text = questionData.question,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                            )
                        }
                    }
                }
            }
        }
        questionData?.let {
            AnswersLayout(answers, selectedAnswerIndex) { index ->
                selectedAnswerIndex = index
            }
        }
    }

    BackHandler {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text(text = "Stop Playing") },
            text = { Text(text = "Are you sure do want to stop playing? You will lose all your progress.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearQuestions()
                    showDialog = false
                    navController.navigateUp()
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text(text = "No")
                }
            }
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnswersLayout(answers: List<String>, selectedIndex: Int, onClick: (Int) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(answers) { index, answer ->

            val transition = updateTransition(
                targetState = index == selectedIndex,
                label = "Answer Item Transitions"
            )

            val percent by transition.animateInt(
                label = "Answer Item Corner",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> 25
                        else -> 50
                    }
                })

            val background by transition.animateColor(
                label = "Answer Item Background",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                }
            )

            val textColor by transition.animateColor(
                label = "Answer Item Text Color",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 70.dp)
                    .clip(RoundedCornerShape(percent))
                    .background(background)
                    .clickable(onClick = { onClick(index) })
                    .padding(16.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Center
            ) {
                AnimatedContent(
                    targetState = answer,
                    transitionSpec = { fadeIn() with fadeOut() }) { text ->
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                }
            }

        }
    }
}

@Composable
private fun ColumnScope.Timer(
    viewModel: GameScreenViewModel,
    onTimeout: () -> Unit,
    onGameComplete: () -> Unit
) {

    var restartFlag by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(1f) }

    val progressAnimationValue by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    LaunchedEffect(restartFlag) {
        while (progressValue > 0f) {
            progressValue -= 0.05f
            delay(500)
        }

        onTimeout()
        delay(800)
        if (viewModel.nextQuestion()) {
            progressValue = 1.1f
            restartFlag = !restartFlag
            return@LaunchedEffect
        }

        onGameComplete()
    }

    LinearProgressIndicator(
        modifier = Modifier
            .align(CenterHorizontally)
            .clip(CircleShape)
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        progress = progressAnimationValue,
    )

}