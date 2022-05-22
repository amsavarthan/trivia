package com.amsavarthan.game.trivia.view.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
    parentNavController: NavController
) {

    val scrollState = rememberScrollState()
    val buttonState by homeScreenViewModel.buttonState.collectAsState()

    val gamesPlayed by gameScreenViewModel.gamesPlayed.collectAsState()
    val energy by gameScreenViewModel.energy.collectAsState()

    Box(modifier = Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            User(name = "Amsavarthan", gamesPlayed = gamesPlayed, energy = energy)
            Spacer(modifier = Modifier.height(16.dp))
        }
        AnimatedContainer(
            targetState = buttonState,
            onExpandAction = {
                homeScreenViewModel.updateButtonState(ButtonState.EXPANDED)
            },
            collapsedContent = { CollapsedContent() },
            expandedContent = {
                StartGameScreen(
                    homeScreenViewModel,
                    gameScreenViewModel,
                    parentNavController,
                    onBack = {
                        homeScreenViewModel.updateButtonState(ButtonState.NORMAL)
                    }
                )
            },
        )
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.AnimatedContainer(
    targetState: ButtonState,
    expandedContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit,
    onExpandAction: () -> Unit,
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
private fun User(name: String, gamesPlayed: Int, energy: Int) {

    Spacer(modifier = Modifier.statusBarsHeight(48.dp))
    Text(
        text = "Hello,",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = "$name!",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(32.dp))
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(80.dp)
            .background(MaterialTheme.colorScheme.primary)
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
            title = "ENERGY",
            value = "$energy",
            shape = RoundedCornerShape(topEndPercent = 16, bottomEndPercent = 16),
            paddingValues = PaddingValues(start = 1.dp)
        )
    }

}

@Composable
private fun UserDetails(content: @Composable RowScope.() -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(4.dp,Alignment.CenterVertically),
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
