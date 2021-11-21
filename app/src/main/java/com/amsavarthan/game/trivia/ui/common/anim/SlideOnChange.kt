package com.amsavarthan.game.trivia.ui.common.anim

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

enum class SlideDirection {
    UP,
    DOWN,
    ADAPTIVE;
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideOnChange(
    targetState: Int,
    clipToContainer: Boolean = false,
    direction: SlideDirection,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            getTransistionSpec(
                direction = direction,
                clipToContainer = clipToContainer
            )
        }
    ) { state ->
        content(state)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<Int>.getTransistionSpec(
    direction: SlideDirection,
    clipToContainer: Boolean
) = when (direction) {
    SlideDirection.UP -> {
        slideInVertically(initialOffsetY = { height -> height }) + fadeIn() with
                slideOutVertically(targetOffsetY = { height -> -height }) + fadeOut()
    }
    SlideDirection.DOWN -> {
        slideInVertically(initialOffsetY = { height -> -height }) + fadeIn() with
                slideOutVertically(targetOffsetY = { height -> height }) + fadeOut()
    }
    SlideDirection.ADAPTIVE -> {
        if (targetState > initialState) {
            slideInHorizontally(initialOffsetX = { height -> height }) + fadeIn() with
                    slideOutHorizontally(targetOffsetX = { height -> -height }) + fadeOut()
        } else {
            slideInHorizontally(initialOffsetX = { height -> -height }) + fadeIn() with
                    slideOutHorizontally(targetOffsetX = { height -> height }) + fadeOut()
        }
    }
} using SizeTransform(clip = clipToContainer)
