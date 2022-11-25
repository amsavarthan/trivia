package com.amsavarthan.game.trivia.ui.createGame.models

data class Item(
    val emoji: String,
    val name: String,
    val id: Int,
)

val categories = listOf(
    Item("🔀", "Random", -1),
    Item("🤓", "General Knowledge", 9),
    Item("📚", "Books", 10),
    Item("🎬", "Film", 11),
    Item("🎵", "Music", 12),
    Item("📺", "Television", 14),
    Item("🎮", "Video Games", 15),
    Item("💻", "Computers", 18),
    Item("📝", "Mathematics", 19),
    Item("🏀", "Sports", 21),

    Item("🎷", "Musicals & Theatres", 13),
    Item("🎲", "Board Games", 16),
    Item("🌱", "Science & Nature", 17),
    Item("🐲", "Mythology", 20),
    Item("🗺", "Geography", 22),
    Item("🎨", "Art", 25),
    Item("🦄", "Animals", 27),
    Item("🚗", "Vehicles", 28),
    Item("📰", "Comics", 29),
    Item("🎎", "Japanese Anime & Manga", 31),
    Item("🤡", "Cartoon & Animations", 32),
)

val difficulties = listOf(
    Item("🔀", "Random", -1),
    Item("😌", "Easy", Difficulty.EASY.ordinal),
    Item("🧐", "Medium", Difficulty.MEDIUM.ordinal),
    Item("🥵", "Hard", Difficulty.HARD.ordinal),
)

val modes = listOf(
    Item("🔀", "Random", -1),
    Item("🅰️🅱️", "Multiple Choice", GameMode.MULTIPLE.ordinal),
    Item("🟢🔴", "True/False", GameMode.BOOLEAN.ordinal),
)

enum class Difficulty(val difficultyName: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard")
}

enum class GameMode(val modeName: String) {
    MULTIPLE("multiple"),
    BOOLEAN("boolean")
}