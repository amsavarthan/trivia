package com.amsavarthan.game.trivia.data.models

data class Category(
    val emoji: String,
    val name: String,
    val code: Int,
    val forPro: Boolean = false
)

val categories = arrayOf(
    Category("🤓", "General Knowledge", 9),
    Category("📚", "Books", 10),
    Category("🎬", "Film", 11),
    Category("🎵", "Music", 12),
    Category("📺", "Television", 14),
    Category("🎮", "Video Games", 15),
    Category("💻", "Computers", 18),
    Category("📝", "Mathematics", 19),
    Category("🏀", "Sports", 21),

    Category("🎷", "Musicals & Theatres", 13, true),
    Category("🎲", "Board Games", 16, true),
    Category("🌱", "Science & Nature", 17, true),
    Category("🐲", "Mythology", 20, true),
    Category("🗺", "Geography", 22, true),
    Category("🎨", "Art", 25, true),
    Category("🦄", "Animals", 27, true),
    Category("🚗", "Vehicles", 28, true),
    Category("📰", "Comics", 29, true),
    Category("🎎", "Japanese Anime & Manga", 31, true),
    Category("🤡", "Cartoon & Animations", 32, true),
)
