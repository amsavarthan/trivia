package com.amsavarthan.game.trivia.ui.common.anim

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class SlideDirection {
    object Up : SlideDirection()
    object Down : SlideDirection()
    object Left : SlideDirection()
    object Right : SlideDirection()
    object AdaptiveHorizontal : SlideDirection()
    object AdaptiveVertical : SlideDirection()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideOnChange(
    modifier: Modifier = Modifier,
    targetState: Int,
    clipToContainer: Boolean = false,
    applyFade: Boolean = false,
    direction: SlideDirection,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = targetState,
        transitionSpec = {
            getTransitionSpec(
                direction = direction,
                clipToContainer = clipToContainer,
                applyFade = applyFade,
            )
        }
    ) { state ->
        content(state)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<Int>.getTransitionSpec(
    direction: SlideDirection,
    clipToContainer: Boolean,
    applyFade: Boolean
): ContentTransform {
    return when (direction) {
        SlideDirection.Up -> {
            (if (applyFade) fadeIn() else EnterTransition.None) + slideInVertically(initialOffsetY = { height -> height }) with
                    (if (applyFade) fadeOut() else ExitTransition.None) + slideOutVertically(
                targetOffsetY = { height -> -height })
        }
        SlideDirection.Down -> {
            (if (applyFade) fadeIn() else EnterTransition.None) + slideInVertically(initialOffsetY = { height -> -height }) with
                    (if (applyFade) fadeOut() else ExitTransition.None) + slideOutVertically(
                targetOffsetY = { height -> height })
        }
        SlideDirection.Left -> {
            (if (applyFade) fadeIn() else EnterTransition.None) + slideInHorizontally(initialOffsetX = { height -> height }) with
                    (if (applyFade) fadeOut() else ExitTransition.None) + slideOutHorizontally(
                targetOffsetX = { height -> -height })
        }
        SlideDirection.Right -> {
            (if (applyFade) fadeIn() else EnterTransition.None) + slideInHorizontally(initialOffsetX = { height -> -height }) with
                    (if (applyFade) fadeOut() else ExitTransition.None) + slideOutHorizontally(
                targetOffsetX = { height -> height })
        }
        SlideDirection.AdaptiveHorizontal -> {
            if (targetState > initialState) {
                getTransitionSpec(SlideDirection.Left, clipToContainer, applyFade)
            } else {
                getTransitionSpec(SlideDirection.Right, clipToContainer, applyFade)
            }
        }
        SlideDirection.AdaptiveVertical -> {
            if (targetState > initialState) {
                getTransitionSpec(SlideDirection.Up, clipToContainer, applyFade)
            } else {
                getTransitionSpec(SlideDirection.Down, clipToContainer, applyFade)
            }
        }
    } using SizeTransform(clip = clipToContainer)
}
