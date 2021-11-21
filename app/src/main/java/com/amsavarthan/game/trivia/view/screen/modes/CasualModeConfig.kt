package com.amsavarthan.game.trivia.view.screen.modes

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.data.models.Category
import com.amsavarthan.game.trivia.data.models.categories
import com.amsavarthan.game.trivia.ui.common.anim.SlideDirection
import com.amsavarthan.game.trivia.ui.common.anim.SlideOnChange
import com.amsavarthan.game.trivia.ui.navigation.Screen

@Composable
fun CasualModeConfig(navController: NavController) {

    var selectedIndex by remember { mutableStateOf(0) }
    var triggerStartGame by remember { mutableStateOf(false) }

    LaunchedEffect(triggerStartGame) {
        if (triggerStartGame) {
            navController.navigate(Screen.COUNT_DOWN.route) {
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SelectedCategoryDetail(selectedIndex, triggerStartGame)
            CategoryList(selectedIndex, triggerStartGame) { clickedIndex ->
                triggerStartGame = (selectedIndex == clickedIndex)
                selectedIndex = clickedIndex
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CategoryList(selectedIndex: Int, triggerStartGame: Boolean, onItemClick: (Int) -> Unit) {
    val listState = rememberLazyListState()

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = !triggerStartGame,
        enter = fadeIn() + slideInVertically { height -> height },
        exit = fadeOut() + slideOutVertically { height -> height }
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        ) {
            itemsIndexed(categories) { index, item ->
                Spacer(modifier = Modifier.width(if (index == 0) 24.dp else 4.dp))
                CategoryItem(item, selectedIndex == index, onClick = {
                    onItemClick(index)
                })
                Spacer(modifier = Modifier.width(if (index == categories.lastIndex) 24.dp else 4.dp))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColumnScope.SelectedCategoryDetail(selectedIndex: Int, triggerStartGame: Boolean) {

    val alpha by animateFloatAsState(
        targetValue = if (categories[selectedIndex].forPro) 1f
        else 0f
    )

    AnimatedVisibility(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        visible = !triggerStartGame,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SlideOnChange(
                targetState = selectedIndex,
                direction = SlideDirection.ADAPTIVE
            ) { index ->
                Text(
                    text = categories[index].emoji,
                    fontSize = 80.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = selectedIndex,
                transitionSpec = { fadeIn() with fadeOut() }
            ) { index ->
                Text(
                    text = categories[index].name,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Click again to start the game",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Badge(
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(alpha)
            ) {
                Text(text = "PRO", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun CategoryItem(category: Category, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .size(60.dp)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(),
                interactionSource = MutableInteractionSource()
            )
            .border(
                if (selected) 0.dp else 2.dp,
                MaterialTheme.colorScheme.secondaryContainer,
                CircleShape
            )
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.background
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = category.emoji, style = MaterialTheme.typography.titleLarge)
    }
}