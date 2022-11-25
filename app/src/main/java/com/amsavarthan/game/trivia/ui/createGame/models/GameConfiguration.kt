package com.amsavarthan.game.trivia.ui.createGame.models

data class GameConfiguration(
    val type: GameConfigurationType,
    val index: Int,
    val value: Int,
) {
    companion object {
        fun parse(input: String): GameConfiguration {
            val splits = input.split('=')

            val type = when (splits[0]) {
                "category" -> GameConfigurationType.CATEGORY
                "difficulty" -> GameConfigurationType.DIFFICULTY
                "type" -> GameConfigurationType.MODES
                else -> throw IllegalStateException("Invalid input = $input")
            }

            val value = splits[1].substringBefore('(').toInt()
            val index = splits[1].substringAfter('(').dropLast(1).toInt()

            return GameConfiguration(type, value, index)

        }
    }

    override fun toString(): String {

        val stringBuilder = StringBuilder()

        when (type) {
            GameConfigurationType.CATEGORY -> stringBuilder.append("category")
            GameConfigurationType.DIFFICULTY -> stringBuilder.append("difficulty")
            GameConfigurationType.MODES -> stringBuilder.append("type")
        }

        stringBuilder.append("=$value($index)")

        return stringBuilder.toString()
    }
}

fun List<GameConfiguration>.toRouteString(
    separator: Char = '_'
): String {
    return map { it.toString() }.reduce { prev, curr ->
        "$prev$separator$curr"
    }
}

sealed class GameConfigurationState {
    object InProgress : GameConfigurationState()
    class Completed(val configurations: List<GameConfiguration>) : GameConfigurationState()
}

enum class GameConfigurationType(
    val title: String,
    val items: List<Item>
) {
    CATEGORY("Choose a category", categories),
    DIFFICULTY("Choose a difficulty", difficulties),
    MODES("Choose game mode", modes);
}

fun GameConfigurationType.hasPrevious(): Boolean {
    return this != GameConfigurationType.CATEGORY
}

fun GameConfigurationType.hasNext(): Boolean {
    return this != GameConfigurationType.MODES
}

fun GameConfigurationType.previous(): GameConfigurationType {
    return when (this) {
        GameConfigurationType.DIFFICULTY -> GameConfigurationType.CATEGORY
        GameConfigurationType.MODES -> GameConfigurationType.DIFFICULTY
        else -> throw IllegalStateException("Previous configuration for $this not found")
    }
}

fun GameConfigurationType.next(): GameConfigurationType {
    return when (this) {
        GameConfigurationType.CATEGORY -> GameConfigurationType.DIFFICULTY
        GameConfigurationType.DIFFICULTY -> GameConfigurationType.MODES
        else -> throw IllegalStateException("Next configuration for $this not found")
    }
}
