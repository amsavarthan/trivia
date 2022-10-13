package com.amsavarthan.game.trivia.data.preference

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository<T> {
    val preferencesFlow: Flow<T>
    suspend fun fetchInitialPreferences(): T
}