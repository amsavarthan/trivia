package com.amsavarthan.game.trivia.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.R
import com.amsavarthan.game.trivia.viewmodel.GameScreenViewModel
import com.amsavarthan.game.trivia.viewmodel.HomeScreenViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight

enum class ButtonState {
    EXPANDED,
    NORMAL;
}

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel,
    gameScreenViewModel: GameScreenViewModel,
    navController: NavController
) {

    val scrollState = rememberScrollState()

    val buttonState by homeScreenViewModel.buttonState.collectAsState()
    val gamesPlayed by gameScreenViewModel.gamesPlayed.collectAsState()
    val triviaPoints by gameScreenViewModel.triviaPoints.collectAsState()

    Box(modifier = Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(gamesPlayed = gamesPlayed, triviaPoints = triviaPoints)
            Spacer(modifier = Modifier.height(16.dp))
        }
        AnimatedContainer(
            targetState = buttonState,
            onExpandAction = { homeScreenViewModel.updateButtonState(ButtonState.EXPANDED) },
            collapsedContent = { CollapsedContent() },
            expandedContent = {
                StartGameScreen(
                    homeScreenViewModel = homeScreenViewModel,
                    gameScreenViewModel = gameScreenViewModel,
                    parentNavController = navController,
                    onBack = { homeScreenViewModel.updateButtonState(ButtonState.NORMAL) }
                )
            },
        )
    }
}

@Composable
private fun Header(
    gamesPlayed: Int,
    triviaPoints: Int
) {

    Spacer(modifier = Modifier.statusBarsHeight(48.dp))
    Text(
        text = stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Created by Amsavarthan Lv",
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(32.dp))

    UserDetails {
        UserDetailItem(
            title = "GAMES PLAYED",
            value = "$gamesPlayed",
            shape = RoundedCornerShape(topStartPercent = 16, bottomStartPercent = 16),
            paddingValues = PaddingValues(end = 1.dp)
        )
        UserDetailItem(
            title = "TRIVIA POINTS",
            value = "$triviaPoints",
            shape = RoundedCornerShape(topEndPercent = 16, bottomEndPercent = 16),
            paddingValues = PaddingValues(start = 1.dp)
        )
    }

}

@Composable
private fun BoxScope.AnimatedContainer(
    targetState: ButtonState,
    onExpandAction: () -> Unit,
    expandedContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit,
) {

    val transition = updateTransition(
        targetState,
        label = "Play Now Button Transition"
    )

    val padding by transition.animateDp(label = "Play Now Button Padding") { state ->
        when (state) {
            ButtonState.NORMAL -> 24.dp
            ButtonState.EXPANDED -> 0.dp
        }
    }

    val height by transition.animateDp(
        label = "Play Now Button Height",
        transitionSpec = { if (initialState == ButtonState.NORMAL) tween(400) else tween(350) }) { state ->
        when (state) {
            ButtonState.NORMAL -> 100.dp
            ButtonState.EXPANDED -> LocalConfiguration.current.screenHeightDp.dp
        }
    }

    val maxWidth by transition.animateDp(label = "Play Now Button MaxWidth") { state ->
        when (state) {
            ButtonState.NORMAL -> 500.dp
            ButtonState.EXPANDED -> Dp.Unspecified
        }
    }

    val corners by transition.animateFloat(label = "Play Now Button Corners") { state ->
        when (state) {
            ButtonState.NORMAL -> 100f
            ButtonState.EXPANDED -> 0f
        }
    }

    val color by transition.animateColor(
        label = "Play Now Button Background Color",
        transitionSpec = {
            if (initialState == ButtonState.EXPANDED) tween(delayMillis = 100) else spring()
        }) { state ->
        when (state) {
            ButtonState.NORMAL -> MaterialTheme.colorScheme.primary
            ButtonState.EXPANDED -> MaterialTheme.colorScheme.background
        }
    }

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .height(height)
            .navigationBarsPadding()
            .padding(padding),
        onClick = onExpandAction,
        indication = if (targetState == ButtonState.NORMAL) rememberRipple() else null,
        color = color,
        shape = RoundedCornerShape(corners),
    ) {
        AnimatedVisibility(
            visible = targetState == ButtonState.NORMAL,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 100,
                )
            ),
            exit = ExitTransition.None
        ) {
            collapsedContent()
        }
        AnimatedVisibility(
            visible = targetState == ButtonState.EXPANDED,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 200,
                )
            ),
            exit = ExitTransition.None
        ) {
            expandedContent()
        }
    }

}

@Composable
private fun CollapsedContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            4.dp,
            Alignment.CenterHorizontally
        ),
    ) {
        Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Play Game")
        Text(text = "PLAY NOW", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun UserDetails(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        content()
    }
}

@Composable
private fun RowScope.UserDetailItem(
    shape: Shape = RectangleShape,
    paddingValues: PaddingValues,
    title: String,
    value: String,
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .padding(paddingValues)
            .height(100.dp),
        onClick = { /*TODO*/ },
        shape = shape,
        indication = rememberRipple(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
