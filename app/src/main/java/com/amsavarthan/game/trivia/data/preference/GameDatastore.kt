package com.amsavarthan.game.trivia.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val GAME_PREFERENCES_NAME = "game_preferences"
private val Context.datastore by preferencesDataStore(GAME_PREFERENCES_NAME)

class GameDatastore @Inject constructor(
    @ApplicationContext context: Context
) {

    private val GAMES_PLAYED = intPreferencesKey("games_played")
    private val TRIVIA_POINTS = intPreferencesKey("trivia_points")

    private val datastore = context.datastore

    val gamesPlayedPreferencesFlow = datastore.data
        .catch {
            if (it !is IOException) throw it
            it.printStackTrace()
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[GAMES_PLAYED] ?: 0
        }

    val triviaPointsPreferencesFlow = datastore.data
        .catch {
            if (it !is IOException) throw it
            it.printStackTrace()
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[TRIVIA_POINTS] ?: 0
        }

    suspend fun incrementGamePlayCount() {
        datastore.edit { preferences ->
            preferences[GAMES_PLAYED] = (preferences[GAMES_PLAYED] ?: 0).inc()
        }
    }

    suspend fun incrementTriviaPoints(points: Int) {
        datastore.edit { preferences ->
            preferences[TRIVIA_POINTS] = (preferences[TRIVIA_POINTS] ?: 0).plus(points)
        }
    }

}