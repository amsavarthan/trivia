@file:OptIn(ExperimentalAnimationApi::class)

package com.amsavarthan.game.trivia.ui.joinGame

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun JoinGameScreen(
    navigator: DestinationsNavigator,
    viewModel: JoinGameScreenViewModel = hiltViewModel(),
) {

    val (digitCount, gameCode) = viewModel.uiState

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.1f))
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(56.dp)
        ) {
            Text(
                text = "Enter game code",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )

            GameCodeInput(
                text = gameCode,
                onTextChange = viewModel::changeGameCode,
                digitCount = digitCount,
                onImeGoClick = { viewModel.navigateToLoadingScreen(navigator, gameCode) }
            )

        }

        ContinueButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = viewModel.isGameCodeFilled,
            icon = Icons.Rounded.ChevronRight,
            onClick = { viewModel.navigateToLoadingScreen(navigator, gameCode) }
        )

    }

}

@Composable
fun ContinueButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 15,
                delayMillis = 30,
                easing = LinearEasing,
            ),
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 15,
                delayMillis = 150,
                easing = LinearEasing,
            )
        ),
    ) {

        val fabScale by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = when (targetState) {
                        EnterExitState.PreEnter, EnterExitState.Visible -> 330
                        EnterExitState.PostExit -> 135
                    },
                    delayMillis = 0,
                    easing = LinearOutSlowInEasing,
                )
            }, label = "FAB scale"
        ) {
            when (it) {
                EnterExitState.PreEnter, EnterExitState.PostExit -> 0f
                EnterExitState.Visible -> 1f
            }
        }

        LargeFloatingActionButton(
            modifier = Modifier
                .padding(bottom = 36.dp)
                .graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                },
            onClick = onClick
        ) {
            val contentScale by transition.animateFloat(
                transitionSpec = {
                    tween(
                        durationMillis = when (targetState) {
                            EnterExitState.PreEnter,
                            EnterExitState.Visible,
                            -> 240
                            EnterExitState.PostExit -> 135
                        },
                        delayMillis = when (targetState) {
                            EnterExitState.PreEnter,
                            EnterExitState.Visible,
                            -> 90
                            EnterExitState.PostExit -> 0
                        },
                        easing = FastOutLinearInEasing,
                    )
                }, label = "FAB content scale"
            ) {
                when (it) {
                    EnterExitState.PreEnter,
                    EnterExitState.PostExit,
                    -> 0f
                    EnterExitState.Visible -> 1f
                }
            }
            Box(Modifier.graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
            }) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = icon,
                    contentDescription = "Next",
                )
            }
        }
    }

}

@Composable
private fun GameCodeInput(
    text: String,
    onTextChange: (String) -> Unit,
    digitCount: Int,
    onImeGoClick: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(modifier = Modifier.focusRequester(focusRequester),
        value = TextFieldValue(
            text,
            selection = TextRange(if (text.isNotBlank()) text.length else 0)
        ),
        onValueChange = {
            if (it.text.length > digitCount) return@BasicTextField
            onTextChange(it.text)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(onGo = {
            if (text.length != digitCount) return@KeyboardActions
            onImeGoClick()
        }),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(digitCount) { index ->
                    DigitView(index, text)
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        })
}


@Composable
private fun DigitView(
    index: Int, pinText: String
) {

    val text = remember(pinText) { if (index >= pinText.length) "" else pinText[index].toString() }
    val percent by animateIntAsState(targetValue = if (text.isBlank()) 25 else 40)

    Box(
        modifier = Modifier
            .width(70.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(percent))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(percent)
            )
            .padding(bottom = 3.dp)
    ) {
        Text(
            text,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }


}
