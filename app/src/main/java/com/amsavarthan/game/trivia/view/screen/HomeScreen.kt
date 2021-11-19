package com.amsavarthan.game.trivia.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight

enum class ButtonState {
    EXPANDED,
    NORMAL;
}

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    var buttonState by remember { mutableStateOf(ButtonState.NORMAL) }
    Box(modifier = Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            User(name = "Amsavarthan", gamesPlayed = 0, energy = 0)
            Spacer(modifier = Modifier.height(16.dp))
        }
        AnimatedContainer(
            targetState = buttonState,
            onExpandAction = { buttonState = ButtonState.EXPANDED },
            collapsedContent = { CollapsedContent() },
            expandedContent = {
                StartGameScreen(onBack = {
                    buttonState = ButtonState.NORMAL
                })
            },
        )
    }
}

@Composable
private fun CollapsedContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Play Game")
        Spacer(modifier = Modifier.width(4.dp))
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
    val height by transition.animateDp(label = "Play Now Button Height") { state ->
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
    val color by transition.animateColor(label = "Play Now Button Background Color") { state ->
        when (state) {
            ButtonState.NORMAL -> MaterialTheme.colorScheme.primary
            ButtonState.EXPANDED -> MaterialTheme.colorScheme.background
        }
    }

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .height(height)
            .widthIn(max = maxWidth)
            .fillMaxWidth()
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
            title = "Games Played",
            value = "${if (gamesPlayed == 0) "-" else gamesPlayed}",
            shape = RoundedCornerShape(topStartPercent = 16, bottomStartPercent = 16),
            paddingValues = PaddingValues(end = 1.dp)
        )
        UserDetailItem(
            title = "Energy",
            value = "${if (energy == 0) "-" else energy}",
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
