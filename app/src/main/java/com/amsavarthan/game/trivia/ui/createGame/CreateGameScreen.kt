@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.amsavarthan.game.trivia.ui.createGame

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.createGame.models.GameConfigurationState
import com.amsavarthan.game.trivia.ui.createGame.models.GameConfigurationType
import com.amsavarthan.game.trivia.ui.createGame.models.Item
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CreateGameScreen(
    navigator: DestinationsNavigator,
    viewModel: CreateGameScreenViewModel = hiltViewModel(),
) {

    val (selectedIndex, configurationType, configurationState) = viewModel.uiState
    val listState = rememberLazyListState()

    LaunchedEffect(configurationType) {
        listState.animateScrollToItem(selectedIndex, -50)
    }

    LaunchedEffect(configurationState) {
        when (configurationState) {
            GameConfigurationState.InProgress -> Unit
            is GameConfigurationState.Completed -> viewModel.navigateToLoadingScreen(
                navigator,
                configurationState.configurations
            )
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.1f))
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        AnimatedContent(
            targetState = configurationType.title,
            transitionSpec = { fadeIn() with fadeOut() }) { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column {

            // This is not a best way to do
            // All the composable are identical yet used like this
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                when (configurationType) {
                    GameConfigurationType.CATEGORY -> CategoryItemDetail(
                        configurationType.items, selectedIndex
                    )
                    GameConfigurationType.DIFFICULTY -> DifficultyItemDetail(
                        configurationType.items, selectedIndex
                    )
                    GameConfigurationType.MODES -> GameModeItemDetail(
                        configurationType.items, selectedIndex
                    )
                }
            }

            Chooser(
                state = listState,
                selectedIndex = selectedIndex,
                configType = configurationType,
                onItemClick = viewModel::updateSelectedIndex,
                onChosen = viewModel::onChosen,
            )
        }
    }

    BackHandler(enabled = configurationType != GameConfigurationType.CATEGORY) {
        if (viewModel.onBack()) return@BackHandler
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CategoryItemDetail(items: List<Item>, selectedIndex: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SlideOnChange(
            targetState = selectedIndex,
            direction = SlideDirection.AdaptiveHorizontal,
            applyFade = true
        ) { index ->
            Text(
                text = items[index].emoji, fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedContent(targetState = selectedIndex,
            transitionSpec = { fadeIn() with fadeOut() }) { index ->
            Text(
                text = items[index].name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Click again to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DifficultyItemDetail(items: List<Item>, selectedIndex: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SlideOnChange(
            targetState = selectedIndex,
            direction = SlideDirection.AdaptiveHorizontal,
            applyFade = true
        ) { index ->
            Text(
                text = items[index].emoji, fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedContent(targetState = selectedIndex,
            transitionSpec = { fadeIn() with fadeOut() }) { index ->
            Text(
                text = items[index].name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Click again to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GameModeItemDetail(items: List<Item>, selectedIndex: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SlideOnChange(
            targetState = selectedIndex,
            direction = SlideDirection.AdaptiveHorizontal,
            applyFade = true
        ) { index ->
            Text(
                text = items[index].emoji, fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedContent(targetState = selectedIndex,
            transitionSpec = { fadeIn() with fadeOut() }) { index ->
            Text(
                text = items[index].name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Click again to start a game",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}


@Composable
private fun Chooser(
    state: LazyListState,
    selectedIndex: Int,
    configType: GameConfigurationType,
    onChosen: () -> Unit,
    onItemClick: (Int) -> Unit,
) {
    SlideOnChange(
        targetState = configType.ordinal, direction = SlideDirection.AdaptiveHorizontal
    ) {
        EmojiList(
            state, configType.items, selectedIndex
        ) { clickedIndex ->
            if (selectedIndex == clickedIndex) {
                onChosen()
                return@EmojiList
            }
            onItemClick(clickedIndex)
        }
    }
}

@Composable
private fun EmojiList(
    state: LazyListState,
    items: List<Item>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
) {
    LazyRow(
        state = state, modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            Spacer(modifier = Modifier.width(if (index == 0) 24.dp else 8.dp))
            EmojiItem(item.emoji, selectedIndex == index, onClick = { onItemClick(index) })
            Spacer(modifier = Modifier.width(if (index == items.lastIndex) 24.dp else 8.dp))
        }
    }
}


@Composable
private fun EmojiItem(text: String, selected: Boolean, onClick: () -> Unit) {

    val percent by animateIntAsState(targetValue = if (selected) 50 else 25)

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(min = 80.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(percent = percent))
            .clickable(
                onClick = onClick,
                indication = rememberRipple(),
                interactionSource = MutableInteractionSource()
            )
            .border(
                if (selected) 0.dp else 2.dp,
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(percent = percent)
            )
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            style = MaterialTheme.typography.displaySmall
        )
    }
}