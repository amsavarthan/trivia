package com.amsavarthan.game.trivia.data.preference

import android.content.Context
import android.util.Log
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
    private val ENERGY = intPreferencesKey("energy")

    private val datastore = context.datastore

    val gamesPlayedPreferencesFlow = datastore.data
        .catch {
            if (it !is IOException) throw it
            it.printStackTrace()
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[GAMES_PLAYED] ?: 0
        }

    suspend fun incrementGamePlayCount() {
        datastore.edit { preferences ->
            preferences[GAMES_PLAYED] = (preferences[GAMES_PLAYED] ?: 0).inc()
        }
    }

    val energyPreferencesFlow = datastore.data
        .catch {
            if (it !is IOException) throw it
            it.printStackTrace()
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[ENERGY] ?: 5
        }

    suspend fun decreaseEnergy() {
        datastore.edit { preferences ->
            preferences[ENERGY] = (preferences[ENERGY] ?: 5).dec()
        }
    }

    suspend fun increaseEnergy() {
        datastore.edit { preferences ->
            val energy = preferences[ENERGY]
            preferences[ENERGY] = when {
                energy == null -> 1
                energy < 5 -> energy.inc()
                else -> return@edit
            }
        }
    }

}