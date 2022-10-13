package com.amsavarthan.game.trivia.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.common.ButtonState
import com.amsavarthan.game.trivia.ui.navigation.AppScreen
import com.amsavarthan.game.trivia.ui.navigation.createRoute
import com.amsavarthan.game.trivia.ui.viewmodel.HomeScreenViewModel

class HomeScreenState(
    val navController: NavController,
) {



}

@Composable
private fun rememberUIState(
    navController: NavController,
) = remember(navController) {
    HomeScreenState(navController)
}

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavController
) {

    val uiState = rememberUIState(navController = navController)

    val gamesPlayed by homeScreenViewModel.gamesPlayed
    val energy by homeScreenViewModel.energy

    Box(modifier = Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            User(name = "Amsavarthan", gamesPlayed = gamesPlayed, energy = energy)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp),
            onClick = {
                navController.navigate(AppScreen.CountDown.createRoute(categories.random().id))
            }
        ) {
            Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Play Game")
            Text(text = "PLAY NOW", style = MaterialTheme.typography.labelLarge)
        }
    }

}


@Composable
private fun BoxScope.ExpandableButton(
    buttonState: ButtonState,
    onClick: () -> Unit,
) {
    val transition = updateTransition(
        buttonState,
        label = "Play Now Button Transition"
    )

    val padding by transition.animateDp(label = "Play Now Button Padding") { state ->
        when (state) {
            ButtonState.Normal -> 24.dp
            ButtonState.Expanded -> 0.dp
        }
    }

    val height by transition.animateDp(
        label = "Play Now Button Height",
        transitionSpec = { if (initialState == ButtonState.Normal) tween(400) else tween(350) }) { state ->
        when (state) {
            ButtonState.Normal -> 100.dp
            ButtonState.Expanded -> LocalConfiguration.current.screenHeightDp.dp
        }
    }

    val maxWidth by transition.animateDp(label = "Play Now Button MaxWidth") { state ->
        when (state) {
            ButtonState.Normal -> 500.dp
            ButtonState.Expanded -> Dp.Unspecified
        }
    }

    val corners by transition.animateFloat(label = "Play Now Button Corners") { state ->
        when (state) {
            ButtonState.Normal -> 100f
            ButtonState.Expanded -> 0f
        }
    }

    val color by transition.animateColor(
        label = "Play Now Button Background Color",
        transitionSpec = {
            if (initialState == ButtonState.Expanded) tween(delayMillis = 100) else spring()
        }) { state ->
        when (state) {
            ButtonState.Normal -> MaterialTheme.colorScheme.primary
            ButtonState.Expanded -> MaterialTheme.colorScheme.background
        }
    }

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .height(height)
            .navigationBarsPadding()
            .padding(padding)
            .clip(RoundedCornerShape(corners))
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = if (buttonState == ButtonState.Normal) rememberRipple() else null,
                onClick = onClick
            ),
        color = color,
    ) {

        AnimatedVisibility(
            visible = buttonState == ButtonState.Normal,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 100,
                )
            ),
            exit = ExitTransition.None
        ) {

        }

        AnimatedVisibility(
            visible = buttonState == ButtonState.Expanded,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 200,
                )
            ),
            exit = ExitTransition.None
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {

            }
        }
    }


}

@Composable
private fun User(name: String, gamesPlayed: Int, energy: Int) {

    Spacer(
        modifier = Modifier
            .windowInsetsTopHeight(
                WindowInsets.statusBars.add(
                    WindowInsets(top = 48.dp)
                )
            )
    )
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

@OptIn(ExperimentalMaterial3Api::class)
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
        shape = shape,
        onClick = {},
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
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
