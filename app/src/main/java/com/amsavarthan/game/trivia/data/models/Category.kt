package com.amsavarthan.game.trivia.data.models

data class Category(
    val emoji: String,
    val name: String,
    val id: Int
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

    Category("🎷", "Musicals & Theatres", 13),
    Category("🎲", "Board Games", 16),
    Category("🌱", "Science & Nature", 17),
    Category("🐲", "Mythology", 20),
    Category("🗺", "Geography", 22),
    Category("🎨", "Art", 25),
    Category("🦄", "Animals", 27),
    Category("🚗", "Vehicles", 28),
    Category("📰", "Comics", 29),
    Category("🎎", "Japanese Anime & Manga", 31),
    Category("🤡", "Cartoon & Animations", 32),
)
